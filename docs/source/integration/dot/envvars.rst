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

The following variables are the paths to the respective APIs. Full paths
need to be provided.

:Variable:
    TOKEN_PATH
:Description:
    The path to use to get to the Token Broker API
:Default:
    /token/jwt/v1/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to /token/jwt/v2 :
            -e TOKEN_PATH='/token/jwt/v2'

:Variable:
    WELL_PATH
:Description:
    The path to use to get to the Wells API
:Default:
    https://demo.slb.com/democore/well/v2/witsml/wells/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/well/v3/witsml/wells/ :
            -e WELL_PATH='https://prod.slb.com/well/v3/witsml/wells/'

:Variable:
    WELL_GQL_PATH
:Description:
    The path to use to get to the Wells GraphQL API
:Default:
    https://demo.slb.com/democore/well/v2/graphql/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/well/v3/graphql :
            -e WELL_GQL_PATH='https://prod.slb.com/well/v3/graphql/'

:Variable:
    WB_PATH
:Description:
    The path to use to get to the Wellbore API
:Default:
    https://demo.slb.com/democore/wellbore/v1/witsml/wellbores/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/v3/witsml/wellbores :
            -e WB_PATH='https://prod.slb.com/v3/witsml/wellbores/

:Variable:
    WB_GQL_PATH
:Description:
    The path to use to get to the Wellbore GraphQL API
:Default:
    https://demo.slb.com/democore/wellbore/v1/graphql/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/wellbore/v3/graphql/ :
            -e WB_GQL_PATH='https://prod.slb.com/wellbore/v3/graphql/'

:Variable:
    TRAJ_PATH
:Description:
    The path to use to get to the Trajectory API
:Default:
    https://demo.slb.com/democore/trajectoryreader/v1/witsml/trajectories/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/trajectoryreader/v3/witsml/trajectories/ :
            -e TRAJ_PATH='https://prod.slb.com/trajectoryreader/v3/witsml/trajectories/'

:Variable:
    TRAJ_GQL_PATH
:Description:
    The path to use to get to the Trajectory GraphQL API
:Default:
    https://demo.slb.com/democore/trajectoryreader/v1/graphql/
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/trajectoryreader/v3/graphql/ :
            -e TRAJ_GQL_PATH='https://prod.slb.com/trajectoryreader/v3/graphql/'

:Variable:
    LOG_DEPTH_BOUNDARY_DATA_PATH
:Description:
    The path to use to query channel depth boundary by specified depth ranges.
:Default:
    https://demo.slb.com/democore/channelreader/v4/channels/depthboundary
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/channelreader/v5/channels/depthboundary :
           -e LOG_DEPTH_BOUNDARY_DATA_PATH='https://prod.slb.com/channelreader/v5/channels/depthboundary'

:Variable:
    LOG_DEPTHDATA_PATH
:Description:
    The path to use to query channel depth data by specifying different query criteria per channel.
:Default:
    https://demo.slb.com/democore/channelreader/v4/channels/depthdata
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/channelreader/v5/channels/depthdata :
            -e LOG_DEPTHDATA_PATH='https://prod.slb.com/channelreader/v5/channels/depthdata'

:Variable:
    LOG_CHANNELS_PATH
:Description:
    1. The path to use to query channel metadata by container (well, wellbore, relog or BHA run) ID
       and other conditions.
    2. This path is also used to add Channels' metadata under a ChannelSet.
    3. This path is also used to get all channels' metadata under a ChannelSet.
:Default:
    https://demo.slb.com/democore/channelreader/v4/witsml/channels/metadata
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/channelreader/v5/witsml/channels/metadata :
            -e LOG_CHANNELS_PATH='https://prod.slb.com/channelreader/v5/witsml/channels/metadata'

:Variable:
    LOG_TIME_BOUNDARY_DATA_PATH
:Description:
    The path to use query channel time boundary by specified time ranges.
:Default:
    https://demo.slb.com/democore/channelreader/v4/channels/timeboundary
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/channelreader/v5/channels/timeboundary :
            -e LOG_TIME_BOUNDARY_DATA_PATH='https://prod.slb.com/channelreader/v5/channels/timeboundary'

:Variable:
    LOG_TIMEDATA_PATH
:Description:
    The path to use to query channel time data by specifying different
    query criteria per channel.
:Default:
    https://demo.slb.com/democore/channelreader/v4/channels/timedata
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/channelreader/v5/channels/timedata :
            -e LOG_TIMEDATA_PATH='https://prod.slb.com/channelreader/v5/channels/timedata"

:Variable:
    LOG_CHANNELS_DATA_PATH
:Description:
    The path to use to add JSON-formatted Channel data under a ChannelSet.
:Default:
    https://demo.slb.com/democore/channelreader/v4/witsml/channels/data
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/channelreader/v5/witsml/channels/data :
            -e LOG_CHANNELS_DATA_PATH='https://prod.slb.com/channelreader/v5/witsml/channels/data'

:Variable:
    LOG_CHANNELSET_PATH
:Description:
    The path to use to create, delete (by UUID), query (by UUID OR as a list),
    replace whole (by UUID), or patch (by UUID) a ChannelSet.
:Default:
    https://demo.slb.com/democore/channelreader/v4/witsml/channelSets
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/channelreader/v5/witsml/channelSets :
           -e LOG_CHANNELSET_PATH='https://prod.slb.com/channelreader/v5/witsml/channelSets'

:Variable:
    LOG_CHANNELSET_UUID_PATH
:Description:
    The path to use to get a ChannelSet Identity by query Well UID,
    Wellbore UID, UID or ChannelSet UUID in WITSML v1.4.1.1 schema.
:Default:
    https://demo.slb.com/democore/channelreader/v4/identities
:Required:
    NO (default will be used)
:Example Environmental Switch in Docker:
    To set to https://prod.slb.com/channelreader/v5/identities :
            -e LOG_CHANNELSET_UUID_PATH='https://prod.slb.com/channelreader/v5/identities'


