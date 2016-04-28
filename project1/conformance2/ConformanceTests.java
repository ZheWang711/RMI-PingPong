package conformance2;

import test.*;

/** Runs all conformance tests on distributed filesystem components.

    <p>
    Tests performed are:
    <ul>
    <li>{@link conformance.rmi.SkeletonTest}</li>
    <li>{@link conformance.rmi.StubTest}</li>
    <li>{@link conformance.rmi.ConnectionTest}</li>
    <li>{@link conformance.rmi.ThreadTest}</li>
    </ul>
 */
public class ConformanceTests
{
    /** Runs the tests.

        @param arguments Ignored.
     */
    public static void main(String[] arguments)
    {
        // Create the test list, the series object, and run the test series.
        @SuppressWarnings("unchecked")
        Class<? extends Test>[]     tests =
            new Class[] {conformance2.rmi.CallTest.class,
                         conformance2.rmi.ArgumentTest.class,
                         conformance2.rmi.ReturnTest.class,
                         conformance2.rmi.ExceptionTest.class,
                         conformance2.rmi.CompleteCallTest.class,
                         conformance2.rmi.ImplicitStubCallTest.class,
                         conformance2.rmi.NullTest.class,
                         conformance2.rmi.RemoteInterfaceTest.class,
                         conformance2.rmi.ListenTest.class,
                         conformance2.rmi.RestartTest.class,
                         conformance2.rmi.NoAddressTest.class,
                         conformance2.rmi.ServiceErrorTest.class,
                         conformance2.rmi.StubTest.class,
                         conformance2.rmi.EqualsTest.class,
                         conformance2.rmi.HashCodeTest.class,
                         conformance2.rmi.ToStringTest.class,
                         conformance2.rmi.SerializableTest.class,
                         conformance2.rmi.OverloadTest.class,
                         conformance2.rmi.ShadowTest.class,
                         conformance2.rmi.InheritanceTest.class,
                         conformance2.rmi.SubclassTest.class,
                         conformance2.rmi.SecurityTest.class,
                         conformance2.rmi.ThreadTest.class};

        Series                      series = new Series(tests);
        SeriesReport                report = series.run(3, System.out);

        // Print the report and exit with an appropriate exit status.
        report.print(System.out);
        System.exit(report.successful() ? 0 : 2);
    }
}
