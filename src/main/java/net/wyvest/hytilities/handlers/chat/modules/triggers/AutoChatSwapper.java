/*
 * Hytilities Reborn - Hypixel focused Quality of Life mod.
 * Copyright (C) 2021  W-OVERFLOW
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.wyvest.hytilities.handlers.chat.modules.triggers;

import cc.woverflow.chatting.chat.ChatTab;
import cc.woverflow.chatting.chat.ChatTabs;
import cc.woverflow.chatting.config.ChattingConfig;
import cc.woverflow.onecore.utils.Utils;
import gg.essential.api.utils.Multithreading;
import gg.essential.universal.wrappers.message.UTextComponent;
import kotlin.Unit;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wyvest.hytilities.Hytilities;
import net.wyvest.hytilities.config.HytilitiesConfig;
import net.wyvest.hytilities.handlers.chat.ChatReceiveModule;
import net.wyvest.hytilities.handlers.language.LanguageData;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class AutoChatSwapper implements ChatReceiveModule {

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public void onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        Multithreading.runAsync(() -> {
            final Matcher statusMatcher = getLanguage().autoChatSwapperPartyStatusRegex.matcher(UTextComponent.Companion.stripFormatting(event.message.getUnformattedText()));
            final Matcher statusMatcher2 = getLanguage().autoChatSwapperPartyStatusRegex2.matcher(UTextComponent.Companion.stripFormatting(event.message.getUnformattedText()));
            if (statusMatcher.matches()) {
                MinecraftForge.EVENT_BUS.register(new ChatChannelMessagePreventer());
                switch (HytilitiesConfig.chatSwapperReturnChannel) {
                    case 0:
                    default:
                        Hytilities.INSTANCE.getCommandQueue().queue("/chat a");
                        if (Hytilities.INSTANCE.isChatting && ChattingConfig.INSTANCE.getChatTabs() && HytilitiesConfig.chattingIntegration) {
                            ChatTab currentTab = ChatTabs.INSTANCE.getCurrentTab();
                            Optional optional = ChatTabs.INSTANCE.getTabs().stream().filter((tab) -> (StringUtils.startsWithIgnoreCase(((ChatTab) tab).getPrefix(), "/ac") || ((ChatTab) tab).getPrefix().isEmpty())).findFirst();
                            if (optional.isPresent()) {
                                ChatTabs.INSTANCE.setCurrentTab(((ChatTab) optional.get()));
                                Utils.sendBrandedNotification("Hytilities Reborn", "Hytilities Reborn has automatically switched to the " + ChatTabs.INSTANCE.getCurrentTab().getName() + " chat tab. Click to revert.", 4f, () -> {
                                    ChatTabs.INSTANCE.setCurrentTab(currentTab);
                                    return Unit.INSTANCE;
                                });
                            }
                        }
                        break;
                    case 1:
                        Hytilities.INSTANCE.getCommandQueue().queue("/chat g");
                        if (Hytilities.INSTANCE.isChatting && ChattingConfig.INSTANCE.getChatTabs() && HytilitiesConfig.chattingIntegration) {
                            ChatTab currentTab = ChatTabs.INSTANCE.getCurrentTab();
                            Optional optional = ChatTabs.INSTANCE.getTabs().stream().filter((tab) -> StringUtils.startsWithIgnoreCase(((ChatTab) tab).getPrefix(), "/gc")).findFirst();
                            if (optional.isPresent()) {
                                ChatTabs.INSTANCE.setCurrentTab(((ChatTab) optional.get()));
                                Utils.sendBrandedNotification("Hytilities Reborn", "Hytilities Reborn has automatically switched to the " + ChatTabs.INSTANCE.getCurrentTab().getName() + " chat tab. Click to revert.", 4f, () -> {
                                    ChatTabs.INSTANCE.setCurrentTab(currentTab);
                                    return Unit.INSTANCE;
                                });
                            }
                        }
                        break;
                    case 2:
                        Hytilities.INSTANCE.getCommandQueue().queue("/chat o");
                        if (Hytilities.INSTANCE.isChatting && ChattingConfig.INSTANCE.getChatTabs() && HytilitiesConfig.chattingIntegration) {
                            ChatTab currentTab = ChatTabs.INSTANCE.getCurrentTab();
                            Optional optional = ChatTabs.INSTANCE.getTabs().stream().filter((tab) -> StringUtils.startsWithIgnoreCase(((ChatTab) tab).getPrefix(), "/oc")).findFirst();
                            if (optional.isPresent()) {
                                ChatTabs.INSTANCE.setCurrentTab(((ChatTab) optional.get()));
                                Utils.sendBrandedNotification("Hytilities Reborn", "Hytilities Reborn has automatically switched to the " + ChatTabs.INSTANCE.getCurrentTab().getName() + " chat tab. Click to revert.", 4f, () -> {
                                    ChatTabs.INSTANCE.setCurrentTab(currentTab);
                                    return Unit.INSTANCE;
                                });
                            }
                        }
                        break;
                }
            } else if (statusMatcher2.matches() && HytilitiesConfig.partySwapper) {
                MinecraftForge.EVENT_BUS.register(new ChatChannelMessagePreventer());
                Hytilities.INSTANCE.getCommandQueue().queue("/chat p");
                if (Hytilities.INSTANCE.isChatting && ChattingConfig.INSTANCE.getChatTabs() && HytilitiesConfig.chattingIntegration) {
                    ChatTab currentTab = ChatTabs.INSTANCE.getCurrentTab();
                    Optional optional = ChatTabs.INSTANCE.getTabs().stream().filter((tab) -> StringUtils.startsWithIgnoreCase(((ChatTab) tab).getPrefix(), "/pc")).findFirst();
                    if (optional.isPresent()) {
                        ChatTabs.INSTANCE.setCurrentTab(((ChatTab) optional.get()));
                        Utils.sendBrandedNotification("Hytilities Reborn", "Hytilities Reborn has automatically switched to the " + ChatTabs.INSTANCE.getCurrentTab().getName() + " chat tab. Click to revert.", 4f, () -> {
                            ChatTabs.INSTANCE.setCurrentTab(currentTab);
                            return Unit.INSTANCE;
                        });
                    }
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return HytilitiesConfig.chatSwapper;
    }

    public static class ChatChannelMessagePreventer {

        private boolean hasDetected;
        private final ScheduledFuture<?> unregisterTimer;

        ChatChannelMessagePreventer() { // if the message somehow doesn't send, we unregister after 20 seconds
            this.unregisterTimer = Multithreading.schedule(() -> { // to prevent blocking the next time it's used
                if (!this.hasDetected) {
                    MinecraftForge.EVENT_BUS.unregister(this);
                }
            }, 20, TimeUnit.SECONDS);
        }


        @SubscribeEvent
        public void checkForAlreadyInThisChannelThing(ClientChatReceivedEvent event) {
            final LanguageData language = Hytilities.INSTANCE.getLanguageHandler().getCurrent();
            final String message = event.message.getUnformattedText();
            if (language.autoChatSwapperAlreadyInChannel.equals(message)
                || (HytilitiesConfig.hideAllChatMessage && language.autoChatSwapperChannelSwapRegex.matcher(message).matches())) {
                this.unregisterTimer.cancel(false);
                this.hasDetected = true;
                event.setCanceled(true);
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }
}
