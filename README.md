# Elementals

Elementals is a gameified to-do application skinned like a fantasy RPG.

*Hold on, isn't that just [Habitica](https://habitica.com)?*

Yes! But different.

# Project overview

## Code organization

The various components of the system are represented by top-level directories (for example, "players" and "tasks").
Inside each component directory, you'll find a variety of modules:

- There will always be a `core` module
- If the component can be delivered by an API, there will be an `api` module
- If the component is delivered by a batch process, there will be some kind of `scheduler` module
- There may be other modules for other adapters related to the component
  (for example, database plugins and wrappers for third-party libraries)

## The `core` module, and anatomy of a use case

The **core** module of a component contains the component's *high-level policy*.
If you open up the core module, you'll find a collection of classes that are
named with verbs (for example, `CreatePlayer` and `CompleteTask`).
These are the *use cases* associated with the component, and are the heart of the application.

Each use case has one public method called `perform`. Each use case also defines an `Outcome`
interface which describes the possible outcomes of running the use case.
For example, the `LevelUpPlayer.Outcome` interface describes the three possible outcomes
of performing the `LevelUpPlayer` use case:

```java
public class LevelUpPlayer {
    public interface Outcome<T> {
        T successfullyUpdatedPlayer(SavedPlayer updatedPlayer);
        T playerCannotLevel();
        T noSuchPlayer();
    }

    public <T> T perform(PlayerId playerId, Outcome<T> outcome) {
        // ...
    }
```

When you invoke the use case, you provide an implementation of the Outcome interface describing
what you want to do in each of the possible scenarios.
For example, when API controllers call the use cases,
they provide Outcome instances that create the appropriate `ResponseEntity`s for each scenario.

## Anatomy of an `api` module

The `api` modules define API endpoints for accessing the use cases.
While these are controller classes, I've named them like `SomeUseCaseEndpoint`
to highlight the fact that each class defines just one endpoint.

The first thing you'll find in each endpoint class is
a request object defining the shape of the request body the endpoint expects (if there is one),
and a `PossibleResponses` class describing all the different responses the endpoint could send back.
For example, the `CreateHabitEndpoint` looks like this:

```java
@RestController
public class CreateHabitEndpoint {

    private static class CreateHabitRequest {
        @JsonProperty
        private String title;
        @JsonProperty
        private String sides;
        @JsonProperty
        private String createdAt;
    }

    private static class PossibleResponses extends ResourceCreatedResponses<SavedHabit>
            implements CreateHabit.Outcome<ResponseEntity> {

        @Override
        public ResponseEntity successfullyCreatedHabit(SavedHabit createdHabit) {
            return ResponseEntity
                    .created(resourceLocation(createdHabit))
                    .body(new HabitResponse(createdHabit));
        }

        ResponseEntity malformedRequest(String error) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(error));
        }

        // ...
    }

    // ...
```

Some `api` modules contain `Request` and `Response` objects describing JSON structures
that are used by multiple endpoints (for example, `HabitResponse` in the above example).

## Deployable applications

The component modules are not standalone applications.
Deployable applications are found in the `applications` directory.
Modules in `applications` import any component modules with functionality they want;
other than that, they should just consist of deployment configuration.