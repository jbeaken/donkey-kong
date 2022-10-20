package org.mzuri.donkeykong.commands

import spock.lang.Shared
import spock.lang.Specification

abstract class AbstractCommandSpec extends Specification {

    @Shared
    String kongControlPlaneUrl = "http://kong-control-plane:8001"
}
