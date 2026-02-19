package com.udaya.util;

import com.udaya.dto.common.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TelegramUtil {

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${telegram.bot-token:}")
	private String botToken;

	@Value("${telegram.chat-id:}")
	private String chatId;

	public void sendErrorNotification(ErrorDetails error) {
		if (botToken == null || botToken.isEmpty() || chatId == null || chatId.isEmpty()) {
			return;
		}

		try {
			String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
			String text = buildMessage(error);

			Map<String, Object> body = new HashMap<>();
			body.put("chat_id", chatId);
			body.put("text", text);
			body.put("parse_mode", "Markdown");

			restTemplate.postForObject(url, body, String.class);
		} catch (Exception e) {
			log.error("Failed to make Telegram API call", e);
		}
	}

	private String buildMessage(ErrorDetails error) {
		String escapedMessage = escapeMarkdown(error.getMessage());
		if (escapedMessage.length() > 3000) {
			escapedMessage = escapedMessage.substring(0, 3000) + "... (truncated)";
		}

		return "*🚨 System Error Alert*\n" + "-------------------------------------\n" + "*. ID:* `" + error.getSystemActivityId() + "`\n" + "*. Message:* " + escapedMessage + "\n" + "*. Endpoint:* `" + error.getEndpoint() + "`\n" + "*. User:* " + escapeMarkdown(error.getUsername()) + "\n" + "*. Time:* " + error.getDateTime() + "\n";
	}

	private String escapeMarkdown(String text) {
		if (text == null) return "";
		return text.replace("_", "\\_").replace("*", "\\*").replace("[", "\\[").replace("`", "\\`");
	}
}
