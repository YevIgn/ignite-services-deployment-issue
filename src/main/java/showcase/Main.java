package showcase;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceConfiguration;
import org.apache.ignite.services.ServiceContext;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.util.Collections;

public class Main {
    private static IgniteConfiguration getConfig(String name, boolean clientMode) {
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();

        ipFinder.setAddresses(Collections.singleton("127.0.0.1:47500..47509"));
        discoverySpi.setIpFinder(ipFinder);
        return new IgniteConfiguration()
                .setIgniteInstanceName(name)
                .setActiveOnStart(true)
                .setDiscoverySpi(discoverySpi)
                .setClientMode(clientMode);
    }

    public static void main(String... args) throws InterruptedException {
        ServiceConfiguration serviceConfiguration = new ServiceConfiguration()
                .setMaxPerNodeCount(1)
                .setService(new TestService())
                .setName("TestService");

        try (Ignite server = Ignition.start(getConfig("server", false));
             Ignite client = Ignition.start(getConfig("client", true))) {

            client.services(client.cluster().forServers())
                    .deploy(serviceConfiguration);
            Thread.sleep(10_000);
        }
    }

    public static class TestService implements Service {
        @IgniteInstanceResource
        private transient Ignite ignite;

        @Override
        public void cancel(ServiceContext serviceContext) {
        }

        @Override
        public void init(ServiceContext serviceContext) throws Exception {
            System.out.println("Ignite node:" + ignite.name());
        }

        @Override
        public void execute(ServiceContext serviceContext) throws Exception {
        }
    }
}
