package io.github.edwardUL99.inject.lite.testing.junit;

import io.github.edwardUL99.inject.lite.testing.TestInjection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.mockito.MockedStatic;

import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestInjectionExtensionTest {
    private MockedStatic<TestInjection> mockedTestInjection;
    private InvocationInterceptor.Invocation<Void> mockInvocation;
    private ReflectiveInvocationContext<Method> mockInvocationContext;
    private ExtensionContext mockExtensionContext;
    private TestInjectionExtension extension;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void init() {
        mockedTestInjection = mockStatic(TestInjection.class);
        mockInvocation = (InvocationInterceptor.Invocation<Void>) mock(InvocationInterceptor.Invocation.class);
        mockInvocationContext = (ReflectiveInvocationContext<Method>) mock(ReflectiveInvocationContext.class);
        mockExtensionContext = mock(ExtensionContext.class);
        extension = new TestInjectionExtension();
    }

    @AfterEach
    public void teardown() {
        if (mockedTestInjection != null)
            mockedTestInjection.close();
    }

    @Test
    public void testInjectionStartAndEnd() throws Throwable {
        String obj = "";
        when(mockExtensionContext.getRequiredTestInstance())
                .thenReturn(obj);
        extension.interceptTestMethod(mockInvocation, mockInvocationContext, mockExtensionContext);
        mockedTestInjection.verify(() -> TestInjection.start(obj));
        mockedTestInjection.verify(TestInjection::end);
        verify(mockInvocation).proceed();
        verify(mockExtensionContext).getRequiredTestInstance();
    }
}
