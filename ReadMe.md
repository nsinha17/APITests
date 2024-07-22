# Airalo API Tests using RestAssured

This repository contains a Java-based REST API test suite using the RestAssured library. The test suite is designed to validate the creation and retrieval of eSIM objects using the Airalo API.

## Table of Contents

- [Overview](#overview)
- [Setup](#setup)
- [Running Tests](#running-tests)
- [Test Details](#test-details)

## Overview

This test suite performs the following operations:

1. **Authenticate** with the Airalo API to obtain an OAuth2 token.
2. **Order eSIM** with a specified package ID and quantity.
3. **Retrieve eSIMs** and validate if the order was successful.
4. **Filter and Verify eSIM Properties** to ensure they match the expected values.

## Setup

To run the tests, you'll need:

- Java 17 or later
- Maven
- RestAssured library
- JUnit 5

### Clone the Repository

```bash
git clone https://github.com/your-repo/api-tests.git
cd api-tests
```

### Build and Install Dependencies

Use Maven to build the project and install the dependencies:

```bash
mvn clean install
```

### Configuration

Update the following constants in the `APITests` class if necessary:

- `CLIENT_ID` - Your client ID for the API
- `CLIENT_SECRET` - Your client secret for the API
- `GRANT_TYPE` - OAuth2 grant type (usually `client_credentials`)

## Running Tests

To execute the tests, run the following Maven command:

```bash
mvn test
```

## Test Details

### Authentication

The `setup()` method sets up the base URI and retrieves the OAuth2 token required for authenticated requests.

### Test: `testESIMCreation()`

- **Order eSIM**: Sends a POST request to order eSIMs with the specified `package_id` and `quantity`.
- **Retrieve eSIMs**: Sends a GET request to retrieve the list of eSIMs.
- **Filter eSIMs**: Filters the eSIMs to find those matching the created eSIM.
- **Verify eSIM Properties**: Asserts that the properties of the retrieved eSIMs match the expected values.

### Utility Methods

- `getOAuth2Token()`: Retrieves an OAuth2 token from the API.
- `getESIMs()`: Retrieves the list of eSIMs.
- `getFilteredEntries()`: Filters the eSIMs by ID.
- `orderESIM()`: Orders an eSIM and returns its ID.
- `verifyESIMProperties()`: Verifies that the properties of the eSIM match the expected values.
