package org.mzuri.donkeykong.containers

import org.mzuri.donkeykong.containers.KongControlPlaneContainer
import org.mzuri.donkeykong.network.KongNetwork
import spock.lang.Requires
import spock.lang.Specification
import org.slf4j.Logger

@Requires({ Boolean.valueOf(sys['test.include.test-containers']) })
class KongControlPlaneContainerSpec extends Specification {

    KongControlPlaneContainer kongControlPlaneContainer

    def "boot up kong control plane successfully"() {
        given: "create network and logger mocks"
        KongNetwork kongNetwork = new KongNetwork()
        Logger logger = Mock()

        when: "create kong control plane container"
        kongControlPlaneContainer = new KongControlPlaneContainer(kongNetwork, logger, true)

        then: "kong control plane container state is correct"
        kongControlPlaneContainer.getAdminApiUrl() == "http://kong-control-plane:8001"
    }
}
