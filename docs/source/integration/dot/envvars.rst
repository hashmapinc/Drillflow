#####################
Environment Variables
#####################

This guide documents how to configure the DrillFlow DOT Valve

************
Introduction
************

There are several environment variables that are used in configuring the DrillFlow DoT Valve.

:Variable:
    VALVE_API_KEY
:Description:
    The API key to be used to authenticate against the API gateway.
:Default:
    No Default
:Required:
    YES
:Example Environmental Switch in Docker:
    To set to xyz : -e VALVE_API_KEY='xyz'

==============
Path Variables
==============

The following variables are the paths to the respctive APIs. The 
construction of the full path will be ${VALVE_BASE_URL}{path} 
where {path} is one of the variables below.

:Variable:
    TOKEN_PATH
:Description:
    The path to use to get to the Token Broker API
:Default:
    /token/jwt/v1/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to /token/jwt/v2 : -e TOKEN_PATH='/token/jwt/v2'

:Variable:
    WELL_PATH
:Description:
    The path to use to get to the Wells API
:Default:
    democore/well/v2/witsml/wells/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to /v3/wells : -e WELL_PATH='/v3/wells'

:Variable:
    WELL_GQL_PATH
:Description:
    The path to use to get to the Wells GraphQL API
:Default:
    democore/well/v2/graphql/
    https://test.com/democore/well/v2/graphql/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to /v3/wellsgql : -e WELL_GQL_PATH='https://baseurl.com/democore/well/v3/wellsgql'

:Variable:
    WB_PATH
:Description:
    The path to use to get to the Wellbore API
:Default:
    democore/wellbore/v1/witsml/wellbores/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to /v3/wellbore : -e WB_PATH='/v3/wellbore'

:Variable:
    WB_GQL_PATH
:Description:
    The path to use to get to the Wellbore GraphQL API
:Default:
    democore/wellbore/v1/graphql/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to /v3/wellboregql : -e WB_PATH='/v3/wellboregql'

:Variable:
    TRAJ_PATH
:Description:
    The path to use to get to the Trajectory API
:Default:
    democore/trajectoryreader/v1/witsml/trajectories/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to /v3/traj : -e WB_PATH='/v3/traj'

:Variable:
    TRAJ_GQL_PATH
:Description:
    The path to use to get to the Trajectory GraphQL API
:Default:
    democore/trajectoryreader/v1/graphql/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to /v3/trajgql : -e WB_PATH='/v3/trajgql'
