package builderb0y.f3screenshot;

import org.junit.jupiter.api.Test;

/**
trick gradle into actually running the test task,
even though there are no tests to run.
this ensures that my doFirst() {} block runs.
*/
public class TheTest {

	@Test
	void theTest() {}
}