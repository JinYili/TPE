import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.example.Main;
import org.example.views.HeaderPanel;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

public class TpeTest extends AssertJSwingJUnitTestCase {
    private FrameFixture window;

    @Override
    protected void onSetUp() {
        application(Main.class).start();
    }

    @Test
    public void applicationTitleAssert(){
        FrameFixture frame = findFrame(new GenericTypeMatcher<Frame>(Frame.class) {
            protected boolean isMatching(Frame frame) {
                return "HKI -> TPE".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(robot());
    }

    @Test
    public void applicationHeaderAssert(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String labelText = "HELSINKI - TAMPERE " + df.format(new Date()).trim();
        GenericTypeMatcher<HeaderPanel> textMatcher = new GenericTypeMatcher<HeaderPanel>(HeaderPanel.class) {
            @Override
            protected boolean isMatching(HeaderPanel headerPanel) {
                JLabel label  = (JLabel) headerPanel.getComponent(0);
                return  labelText.equals(label.getText());
            }
        };
    }
}
