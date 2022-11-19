package soulboundarmory.module.text;

public class FormattingExtensions {
	public static final char[] overlineCodes = {
		4096,
		4097,
		4098,
		4099,
		4100,
		4101,
		};

	public static final ExtendedFormatting overline0 = FormattingRegistry.register("OVERLINE0", overlineCodes[0], true).formatter(new OverlineFormatter(0));
	public static final ExtendedFormatting overline1 = FormattingRegistry.register("OVERLINE1", overlineCodes[1], true).formatter(new OverlineFormatter(1));
	public static final ExtendedFormatting overline2 = FormattingRegistry.register("OVERLINE2", overlineCodes[2], true).formatter(new OverlineFormatter(2));
	public static final ExtendedFormatting overline3 = FormattingRegistry.register("OVERLINE3", overlineCodes[3], true).formatter(new OverlineFormatter(3));
	public static final ExtendedFormatting overline4 = FormattingRegistry.register("OVERLINE4", overlineCodes[4], true).formatter(new OverlineFormatter(4));
	public static final ExtendedFormatting overline5 = FormattingRegistry.register("OVERLINE5", overlineCodes[5], true).formatter(new OverlineFormatter(5));
}
