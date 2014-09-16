package jenkins.plugins.model;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by aidan on 16/09/14.
 */
public class MTTFMetricTest {
    private static final long TODAY = new Date().getTime();
    private static final BuildMessage FIRST_BUILD = new BuildMessage(TODAY, BuildMessage.BUILD_SUCCESS);
    private static final BuildMessage SECOND_BUILD = new BuildMessage(TODAY + 1000, BuildMessage.BUILD_FAILED);
    private static final BuildMessage THIRD_BUILD = new BuildMessage(TODAY + 2000, BuildMessage.BUILD_FAILED);
    private static final BuildMessage FOURTH_BUILD = new BuildMessage(TODAY + 3000, BuildMessage.BUILD_SUCCESS);
    private static final BuildMessage FIFTH_BUILD = new BuildMessage(TODAY + 4000, BuildMessage.BUILD_FAILED);
    private static final BuildMessage SIXTH_BUILD = new BuildMessage(TODAY + 5000, BuildMessage.BUILD_SUCCESS);

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void should_return_0_second_when_first_success_build() {
        runAndVerifyResult(Lists.newArrayList(FIRST_BUILD), 0L, 0);
    }
    @Test
    public void should_return_0_second_when_2_failed_builds() {
        List<BuildMessage> builds = Lists.newArrayList(SECOND_BUILD, THIRD_BUILD);
        runAndVerifyResult(builds, 0, 0);
    }
    @Test
    public void should_return_0_seconds_when_2_success_builds() {
        List<BuildMessage> builds = Lists.newArrayList(FIRST_BUILD, FOURTH_BUILD);
        runAndVerifyResult(builds, 0, 0);
    }
    @Test
    public void should_0_seconds_when_1_failed_and_1_success_builds() {
        List<BuildMessage> builds = Lists.newArrayList(THIRD_BUILD, FOURTH_BUILD);
        runAndVerifyResult(builds, 0, 0);
    }
    @Test
    public void should_correct_metric_when_1_success_and_1_failed_builds() {
        List<BuildMessage> builds = Lists.newArrayList(FIRST_BUILD, SECOND_BUILD);
        runAndVerifyResult(builds, 1000L, 1);
    }
    @Test
    public void should_correct_metric_when_2_success_and_1_failed_builds() {
        List<BuildMessage> builds = Lists.newArrayList(FIRST_BUILD, FOURTH_BUILD, FIFTH_BUILD);
        runAndVerifyResult(builds, 4000L, 1);
    }
    @Test
    public void should_return_failed_info_when_have_all_builds() {
        List<BuildMessage> builds = Lists.newArrayList(FIRST_BUILD, SECOND_BUILD, THIRD_BUILD,
                FOURTH_BUILD, FIFTH_BUILD, SIXTH_BUILD);
        runAndVerifyResult(builds, 1000L, 2);
    }
    private void runAndVerifyResult(List<BuildMessage> builds, long expectTime, int expectCount) {
        AggregateBuildMetric mttfMetric = new MTTFMetric("test", builds);
        assertEquals("Metric Name", "test", mttfMetric.getName());
        assertEquals("MTTR Metric", expectTime, mttfMetric.calculateMetric());
        assertEquals("Build Count", expectCount, mttfMetric.getBuildCount());
    }
}