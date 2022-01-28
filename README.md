# ChatSystem

Project presented as a partial requirement for the approval of the course Conception/Programmation Orientée Object in the first semester of the 4th year of Ingénierie Informatique et Reséaux at INSA Toulouse

## Authors
The project was modelled and implemented by the following students:
- DA COSTA MAMEDE CORRÊA Vitor
- ORGANISTA CALDERÓN José Daniel
- LÓPEZ ROMERO Luis Alberto

### Group
The Group TD for this subject was B1 for José and Luis, and B2 for Vitor
The teacher responsable for the TD group was M. Bit-Monnot

## Requirements

All project requirements, for both function and non-functional requirements, as well as a general explanation of how the software should work can be checked [here](https://moodle.insa-toulouse.fr/pluginfile.php/26955/mod_resource/content/1/INSA_COO_POO_URD_v3.1.pdf)

## Directory structure
```
Model - contains diagrams-related files

DiagramImages - contains the same diagrams as in the Model folder, but in JPG format

Project - contains project-related files
│
└───makefile - commands to build and run the project
│
└───src - contains the java project files
│
└───pom.xml - maven project usual pom file
```

## Conception
The project conception and modelling was all done using the UML notation, more specifically with the open source software [Modelio](https://www.modelio.org/), the whole folder can be imported to the software, there are also screenshots available at the same folder.

## Project Management

For the purpose of time-management and accompaniment of the project development in respect to the project deadlines, the scrum agile method was applied, all the user-stories, tasks, sprints and more info can be seen [here](https://vitor-maco.atlassian.net/jira/software/projects/AG/boards/1)

## Build and usage instructions

- Clone this Github Repository
- Change the local network to be used in BASE_IP property of the file `/Project/src/main/java/utils/config.properties`
- Run a `make` to run the programs, or a `make install` to generate the jar file
- To use the database you should be connected to the INSA network, if you're not it is necessary to connect to it via a VPN.
