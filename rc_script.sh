#!/bin/bash
# RC Script

RC_VERSION=1.13.0
RC_NUM=2

git checkout main
git pull
git push origin main:rel-${RC_VERSION}-RC


git checkout rel-${RC_VERSION}-RC
git tag -a "rel-${RC_VERSION}-RC-${RC_NUM}" -m "rel-${RC_VERSION}-RC-${RC_NUM}"
git push origin --tags


# Created Branch and Tag Examples
# (Branch) https://github.com/vivo-project/VIVO/tree/rel-1.11.1-RC
# (Tag) https://github.com/vivo-project/VIVO/tree/rel-1.11.1-RC-1
# (Branch) https://github.com/vivo-project/Vitro/tree/rel-1.11.1-RC
# (Tag) https://github.com/vivo-project/Vitro/tree/rel-1.11.1-RC-1
