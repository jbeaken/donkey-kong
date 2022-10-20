package org.mzuri.donkeykong.containers

import org.mzuri.donkeykong.containers.KongToolsContainer
import org.mzuri.donkeykong.network.KongNetwork
import org.slf4j.Logger
import spock.lang.Specification

class KongToolsContainerSpec extends Specification {

    KongToolsContainer kongToolsContainer

    def "boot up kong control plane successfully"() {
        given: "create network and logger mocks"
        KongNetwork kongNetwork = new KongNetwork()
        Logger logger = Mock()

        when: "create kong tools container"
        kongToolsContainer = new KongToolsContainer(kongNetwork, logger)

        then: "kong toolscontainer state is correct"
        kongToolsContainer != null
    }
}
