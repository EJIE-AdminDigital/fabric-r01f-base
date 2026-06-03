# Layers in the Software Architecture of Fabric / Platea-Web

This document describes the structure of the layer definition of the Platea-Web project developed on the Fabric framework, incorporating nomenclature and architectural patterns commonly used in enterprise applications in Java.

The application is organized following a layered architecture. Each layer has clearly defined responsibilities and communicates with adjacent layers through well-defined interfaces, promoting separation of concerns and maintainability.

The layers are listed from the closest to the data source, typically a database, up to the user interface.

# Logical Diagram

┌────────────┐
│   View     │
└────┬───────┘
     │ 
┌────▼───────┐
│ Presenter  │
└────┬───────┘
     │ 
┌────▼───────┐
│ Mediator   │
└────┬───────┘
     │ 
┌────▼────────────────┐
│ Client API Delegate │
└────┬────────────────┘
     │ 
┌────▼──────────┐
│ ServiceIface  │
└────┬──────────┘
     │ 
┌────▼──────────┐
│ ServiceImpl   │
└────┬──────────┘
     │ 
┌────▼──────────┐
│ SvcDelegate   │
└────┬──────────┘
     │ 
┌────▼──────────┐
│ DAO / Repo    │
└────┬──────────┘
     │ 
┌────▼──────────┐
│  DataSource   │
└───────────────┘

# In the CORE

## Data Access / DAO
The data access layer encapsulates access to the database using JPA (currently EclipseLink, with support for Hibernate being added).

For the conversion between the business model and the database model, Transfer classes are used, which are responsible for performing bidirectional mapping between JPA entities and model objects.

## Service Delegate
This layer is responsible for parameter validation and orchestration.

In the case of services that do not require data access, such as an export, this is the last layer and consequently the one that performs the work. In the case of services that require data access, it will use the classes from the data access layer.

## Service Impl
This layer is responsible for providing the security layer and uses the service delegates. It cannot have any implemented logic, only use the delegate.

# In the API

## Service Interface
For each service exposed in the CORE, there should be an interface that defines it in such a way that all layers implement that interface. This acts as an API Contract.

## Client API Delegate
Provides a single point for the UI to consume services, hiding transport details (HTTP, libraries, etc.).

# In the UI

## Mediator
Coordinates interaction between multiple APIs when the view needs data from different sources. Implements the Mediator pattern and, in complex cases, the Facade pattern, using the Client API Delegate.

## Presenter
The presenter is the first layer of the API or business logic used from the interface. It typically implements subscribers that contain the logic to be executed when the API response is received. It uses the mediator.

## View
The graphical interface of pages is built using Vaadin.