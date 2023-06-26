package app.revanced.integrations.patches.ads;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import app.revanced.integrations.patches.utils.NavBarIndexPatch;
import app.revanced.integrations.patches.utils.PatchStatus;
import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.shared.PlayerType;


public class ByteBufferFilterPatch {
    private static final List<String> ignoredList = Arrays.asList(
            "ContainerType|video_action_button",
            "avatar",
            "compact_channel",
            "description",
            "grid_video",
            "metadata",
            "thumbnail",
            "_menu",
            "-button",
            "-count",
            "-space"
    );

    private static final List<String> header = Arrays.asList(
            "YTSans-SemiBold",
            "sans-serif-medium",
            "shelf_header"
    );

    public static ByteBuffer bytebuffer;

    public static boolean filter(String path, String value) {
        if (ignoredList.stream().anyMatch(path::contains))
            return false;

        if (path.contains("library_recent_shelf")) {
            NavBarIndexPatch.setCurrentNavBarIndex(4);
        } else if (SettingsEnum.HIDE_SUGGESTIONS_SHELF.getBoolean() && path.contains("horizontal_video_shelf")) {
            return NavBarIndexPatch.isNoneLibraryTab();
        }

        final String charset = new String(bytebuffer.array(), StandardCharsets.UTF_8);
        int count = 0;

        if (PatchStatus.GeneralAds()) {
            if (SettingsEnum.HIDE_FEED_SURVEY.getBoolean() &&
                    value.contains("_survey")) count++;

            if (SettingsEnum.HIDE_OFFICIAL_HEADER.getBoolean() &&
                    Stream.of("shelf_header")
                            .allMatch(value::contains) &&
                    Stream.of("YTSans-SemiBold", "sans-serif-medium")
                            .allMatch(charset::contains)) count++;
        }

        if (PatchStatus.SuggestedActions() && !PlayerType.getCurrent().isNoneOrHidden()) {
            if (SettingsEnum.HIDE_SUGGESTED_ACTION.getBoolean() &&
                    value.contains("suggested_action")) count++;
        }

        return count > 0;
    }
}
