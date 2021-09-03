package test_locally.util;

import com.slack.api.bolt.tester.slackapi.util.RandomValueGenerator;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class RandomIdValueGeneratorTest {

    @Test
    public void generateID() {
        String teamId = RandomValueGenerator.generateSlackIDString("T", 11);
        assertTrue(teamId.startsWith("T"));
        assertThat(teamId.length(), is(11));
    }

    @Test
    public void generateNumericString() {
        String value = RandomValueGenerator.generateNumericString(11);
        assertThat(value.length(), is(11));
    }
}
