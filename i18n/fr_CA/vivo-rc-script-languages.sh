#!/bin/bash
# RC Script for languages

RC_VERSION=1.11.1
RC_NUM=1

git checkout master
git pull
git push origin master:rel-${RC_VERSION}-RC


git checkout rel-${RC_VERSION}-RC
git tag -a "rel-${RC_VERSION}-RC-${RC_NUM}" -m "rel-${RC_VERSION}-RC-${RC_NUM}"
git push origin --tags


# Created Branch and Tag Examples
# (Branch) https://github.com/vivo-project/Vitro-languages/tree/rel-1.11.1-RC
# (Tag) https://github.com/vivo-project/Vitro-languages/tree/rel-1.11.1-RC-1
# (Branch) https://github.com/vivo-project/VIVO-languages/tree/rel-1.11.1-RC
# (Tag) https://github.com/vivo-project/VIVO-languages/tree/rel-1.11.1-RC-1
