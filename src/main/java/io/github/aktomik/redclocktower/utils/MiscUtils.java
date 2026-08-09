package io.github.aktomik.redclocktower.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

public class MiscUtils {
	private MiscUtils() {}

	// ⓪①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳
	// ㉑㉒㉓㉔㉕㉖㉗㉘㉙㉚㉛㉜㉝㉞㉟㊱㊲㊳㊴㊵㊶㊷㊸㊹㊺㊻㊼㊽㊾㊿
	// ⓿❶❷❸❹❺❻❼❽❾❿⓫⓬⓭⓮⓯⓰⓱⓲⓳⓴
	public static String digitInCircle(int digit) {
		if (digit < 0 || digit > 50) return Integer.toString(digit);
		return "⓪①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳㉑㉒㉓㉔㉕㉖㉗㉘㉙㉚㉛㉜㉝㉞㉟㊱㊲㊳㊴㊵㊶㊷㊸㊹㊺㊻㊼㊽㊾㊿".substring(digit, digit + 1);
	}

	public static final JoinConfiguration oxfordJoinConfig = JoinConfiguration.builder()
		.separator(Component.text(", "))
		.lastSeparator(Component.text(" and "))
		.lastSeparatorIfSerial(Component.text(","))
		.build();
}
