# VIVO: Connect, Share, Discover

[![Build](https://github.com/vivo-project/VIVO/workflows/Build/badge.svg)](https://github.com/vivo-project/VIVO/actions?query=workflow%3ABuild) [![Deploy](https://github.com/vivo-project/VIVO/workflows/Deploy/badge.svg)](https://github.com/vivo-project/VIVO/actions?query=workflow%3ADeploy) [![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.2639714.svg)](https://doi.org/10.5281/zenodo.2639713)

VIVO is an open source semantic web tool for research discovery -- finding people and the research they do.

VIVO supports editing, searching, browsing and visualizing research activity in order to discover people, programs, 
facilities, funding, scholarly works and events. VIVO's search returns results faceted by type for rapid retrieval of 
desired information across disciplines.

## Resources

### VIVO Project web site
[https://vivo.lyrasis.org/](https://vivo.lyrasis.org/)

### VIVO Project Wiki
https://wiki.lyrasis.org/display/VIVO/

### Installation Instructions

Installation instructions for all releases can be found at this location on the wiki:  
https://wiki.lyrasis.org/display/VIVODOC/All+Documentation

When you select the wiki pages for technical documentation for the release you would like to install at https://wiki.lyrasis.org/display/VIVODOC/All+Documentation, please open the Installing VIVO section and follow the instructions.

### Docker

VIVO docker container is available at [vivoweb/vivo](https://hub.docker.com/r/vivoweb/vivo) with accompanying [vivoweb/vivo-solr](https://hub.docker.com/r/vivoweb/vivo-solr). These can be used independently or with docker-compose.

#### Build Args

Build args used at time of building the Docker image.

| Variable                      | Description                  | Default                              |
| ----------------------------- | -----------------------------| ------------------------------------ |
| USER_ID                       | User id                      | 3001                                 |
| USER_NAME                     | User name                    | vivo                                 |
| USER_HOME_DIR                 | User home directory          | /home/vivo                           |

#### Environment

| Variable                      | Description                                                      | Default                                         |
| ----------------------------- | ---------------------------------------------------------------- | ----------------------------------------------- |
| TOMCAT_CONTEXT_PATH           | Tomcat webapp context path                                       | ROOT                                            |
| VIVO_HOME                     | VIVO home directory in container                                 | /usr/local/vivo/home                            |
| TDB_FILE_MODE                 | TDB file mode                                                    | direct                                          |
| ROOT_USER_ADDRESS             | Root user email address                                          | vivo_root@mydomain.edu                          |
| DEFAULT_NAMESPACE             | Default namespace                                                | http://vivo.mydomain.edu/individual/            |
| SOLR_URL                      | VIVO Solr URL                                                    | http://localhost:8983/solr/vivocore             |
| SELF_ID_MATCHING_PROPERTY     | Individual property associated with user account                 | http://vivo.mydomain.edu/ns#networkId           |
| EMAIL_SMTP_HOST               | Email SMTP host                                                  | not defined                                     |
| EMAIL_PORT                    | Email server port                                                | not defined                                     |
| EMAIL_USERNAME                | Email server username                                            | not defined                                     |
| EMAIL_PASSWORD                | Email server password                                            | not defined                                     |
| EMAIL_REPLY_TO                | Reply to email address                                           | not defined                                     |
| LANGUAGE_FILTER_ENABLED       | Enable language filter to respect browser Accept-Language header | not defined                                     |
| FORCE_LOCALE                  | Ignore browser Accept-Language header                            | not defined                                     |
| SELECTABLE_LOCALES            | Selectable locales                                               | not defined                                     |
| RESET_CORE                    | Reset VIVO Solr core                                             | false                                           |
| RESET_HOME                    | Reset VIVO home directory                                        | false                                           |
| LOAD_SAMPLE_DATA              | Load sample data                                                 | false                                           |
| SAMPLE_DATA_REPO_URL          | Sample data GitHub repository                                    | https://github.com/vivo-project/sample-data.git |
| SAMPLE_DATA_BRANCH            | Sample data GitHub repository branch                             | main                                            |
| SAMPLE_DATA_DIRECTORY         | Sample data directory                                            | openvivo                                        |
| RECONFIGURE                   | Whether to update runtime properties and application setup       | false                                           |

> Both RESET_HOME and LOAD_SAMPLE_DATA must be true in order to load sample data.

#### Running VIVO from published Docker images.

Create a docker [network](https://docs.docker.com/engine/reference/commandline/network_create/).
```
docker network create vivo_net
```

Run vivo-solr [detached](https://docs.docker.com/engine/reference/run/#detached-vs-foreground) with [port forwarding](https://docs.docker.com/engine/reference/run/#expose-incoming-ports) and on a above [network](https://docs.docker.com/engine/reference/run/#network-settings).
```
docker run -d -p 8983:8983 --hostname solr --network vivo_net vivoweb/vivo-solr
```

Run vivo with [port forwarding](https://docs.docker.com/engine/reference/run/#expose-incoming-ports), on a above [network](https://docs.docker.com/engine/reference/run/#network-settings), defining Solr URL [environment variable](https://docs.docker.com/engine/reference/run/#env-environment-variables), and local [volume](https://docs.docker.com/engine/reference/run/#volume-shared-filesystems) mounted to VIVO home directory.
```
docker run -p 8080:8080 --network vivo_net -e "SOLR_URL=http://solr:8983/solr/vivocore" -v "./vivo-home:/opt/vivo/home" vivoweb/vivo
```

### Docker Compose

Docker Compose environment variables:

.env defaults
```
LOCAL_SOLR_PORT=8983
LOCAL_SOLR_DATA=./vivo-solr
RESET_CORE=false

LOCAL_VIVO_PORT=8080
LOCAL_VIVO_HOME=./vivo-home
RESET_HOME=false

VIVO_HOME=/usr/local/vivo/home

VERBOSE=no

```

- `LOCAL_SOLR_PORT`: VIVO Solr port on your host machine. Port must not be in use.
- `LOCAL_SOLR_DATA`: VIVO Solr data directory on your host machine which will mount to volume in Solr docker container. Set this environment variable to persist your VIVO Solr data on your host machine.
- `RESET_CORE`: Convenience to reset VIVO Solr core when starting container. **Caution**, will require complete reindex.

- `LOCAL_VIVO_PORT`: VIVO Tomcat port on your host machine. Port must not be in use.
- `LOCAL_VIVO_HOME`: VIVO home directory on your host machine which will mount to volume in docker container. Set this environment variable to persist your VIVO data on your host machine.
- `RESET_HOME`: Convenience to reset VIVO home when starting container. **Caution**, will delete local configuration, content, and configuration model.

- `VIVO_HOME`: VIVO home directory in the container.

- `VERBOSE`: Enable start.sh script debugging.

Build and start VIVO using Docker Compose.

Before building VIVO, you will also need to clone (and switch to the same branch, if other than main) of [Vitro](https://github.com/vivo-project/Vitro). The Vitro project must be cloned to a sibling directory next to VIVO so that it can be found during the build.

Build and start VIVO.

```
mvn clean package
docker-compose up
```

### Docker Image

To build and run local Docker image.

```
docker build -t vivoweb/vivo:development .
docker run -p 8080:8080 vivoweb/vivo:development
```

## Community
There are several ways to contact and join the VIVO community. All of them are listed at https://vivo.lyrasis.org/contact/

## Contributing Code
If you would like to contribute code to the VIVO project, please read instructions at [this page](https://github.com/vivo-project/VIVO/wiki/Development-Processes#process-for-suggesting-contribution). Contributors welcome!

## Citing VIVO
If you are using VIVO in your publications or projects, please cite the software paper in the Journal of Open Source Software:

* Conlon et al., (2019). VIVO: a system for research discovery. Journal of Open Source Software, 4(39), 1182, https://doi.org/10.21105/joss.01182

### BibTeX
```tex
@article{Conlon2019,
  doi = {10.21105/joss.01182},
  url = {https://doi.org/10.21105/joss.01182},
  year = {2019},
  publisher = {The Open Journal},
  volume = {4},
  number = {39},
  pages = {1182},
  author = {Michael Conlon and Andrew Woods and Graham Triggs and Ralph O'Flinn and Muhammad Javed and Jim Blake and Benjamin Gross and Qazi Asim Ijaz Ahmad and Sabih Ali and Martin Barber and Don Elsborg and Kitio Fofack and Christian Hauschke and Violeta Ilik and Huda Khan and Ted Lawless and Jacob Levernier and Brian Lowe and Jose Martin and Steve McKay and Simon Porter and Tatiana Walther and Marijane White and Stefan Wolff and Rebecca Younes},
  title = {{VIVO}: a system for research discovery},
  journal = {Journal of Open Source Software}
}
