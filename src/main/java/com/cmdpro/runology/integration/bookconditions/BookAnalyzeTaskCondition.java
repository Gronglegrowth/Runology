package com.cmdpro.runology.integration.bookconditions;

import com.cmdpro.runology.Runology;
import com.cmdpro.runology.api.AnalyzeTask;
import com.cmdpro.runology.api.AnalyzeTaskSerializer;
import com.cmdpro.runology.api.RunologyUtil;
import com.cmdpro.runology.moddata.PlayerModData;
import com.cmdpro.runology.moddata.PlayerModDataProvider;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionEntryContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BookAnalyzeTaskCondition extends BookCondition {

    public ResourceLocation advancementId;
    public AnalyzeTask[] tasks;
    public Component advancement;
    public boolean hasAdvancement;
    public BookAnalyzeTaskCondition(Component component, AnalyzeTask[] tasks, ResourceLocation advancementId, boolean hasAdvancement) {
        super(component);
        this.tooltip = component;
        this.advancementId = advancementId;
        this.tasks = tasks;
        this.hasAdvancement = hasAdvancement;
    }

    public static BookAnalyzeTaskCondition fromJson(JsonObject json) {
        ResourceLocation advancementId = new ResourceLocation("", "");
        boolean hasAdvancement = false;
        if (json.has("advancement_id")) {
            hasAdvancement = true;
            advancementId = new ResourceLocation(GsonHelper.getAsString(json, "advancement_id"));
        }
        List<AnalyzeTask> tasks = new ArrayList<>();
        if (json.has("tasks")) {
            for (JsonElement i : json.get("tasks").getAsJsonArray()) {
                JsonObject obj = i.getAsJsonObject();
                tasks.add(RunologyUtil.ANALYZE_TASKS_REGISTRY.get().getValue(ResourceLocation.tryParse(obj.get("type").getAsString())).fromJson(obj));
            }
        }


        //default tooltip
        Component tooltip = Component.literal("");
        if (json.has("tooltip")) {
            tooltip = tooltipFromJson(json);
        }

        return new BookAnalyzeTaskCondition(tooltip, tasks.toArray(new AnalyzeTask[0]), advancementId, hasAdvancement);
    }

    @Override
    public List<Component> getTooltip(BookConditionContext context) {
        List<Component> list = new ArrayList<>();
        if (!tooltip.getString().equals("")) {
            list.add(tooltip);
        }
        list.add(Component.translatable("book.runology.condition.analyze.ln1"));
        if (hasAdvancement) {
            list.add(Component.translatable("book.runology.condition.analyze.ln2", Component.translatable(makeDescriptionId("advancements", advancementId) + ".title")));
        }
        return list;
    }
    public static String makeDescriptionId(String pType, @Nullable ResourceLocation pId) {
        if (pId.getNamespace().equals("minecraft")) {
            return pId == null ? pType + ".unregistered_sadface" : pType + "." + pId.getPath().replace('/', '.');
        } else {
            return pId == null ? pType + ".unregistered_sadface" : pType + "." + pId.getNamespace() + "." + pId.getPath().replace('/', '.');
        }
    }

    public static BookAnalyzeTaskCondition fromNetwork(FriendlyByteBuf buffer) {
        var tooltip = buffer.readBoolean() ? buffer.readComponent() : null;
        var hasAdvancement = buffer.readBoolean();
        var advancementId = buffer.readResourceLocation();
        var knowledge = buffer.readList(BookAnalyzeTaskCondition::readTask).toArray(new AnalyzeTask[0]);
        return new BookAnalyzeTaskCondition(tooltip, knowledge, advancementId, hasAdvancement);
    }
    public static AnalyzeTask readTask(FriendlyByteBuf buffer) {
        ResourceLocation resourceLocation = buffer.readResourceLocation();
        return RunologyUtil.ANALYZE_TASKS_REGISTRY.get().getValue(resourceLocation).fromNetwork(buffer);
    }
    public static void writeTask(FriendlyByteBuf buffer, AnalyzeTask task) {
        buffer.writeResourceLocation(RunologyUtil.ANALYZE_TASKS_REGISTRY.get().getKey(task.getSerializer()));
        task.getSerializer().toNetwork(task, buffer);
    }

    @Override
    public ResourceLocation getType() {
        return new ResourceLocation(Runology.MOD_ID, "analyze");
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.tooltip != null);
        if (this.tooltip != null) {
            buffer.writeComponent(this.tooltip);
        }
        buffer.writeBoolean(hasAdvancement);
        buffer.writeResourceLocation(this.advancementId);
        buffer.writeCollection(Arrays.stream(tasks).toList(), BookAnalyzeTaskCondition::writeTask);
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (context instanceof BookConditionEntryContext context2) {
                AtomicReference<PlayerModData> data = new AtomicReference<>();
                player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent((data2) -> {
                    data.set(data2);
                });
                if (data.get().getUnlocked().containsKey(context.getBook().getId())) {
                    if (data.get().getUnlocked().get(context.getBook().getId()).contains(context2.getEntry().getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}