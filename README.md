This is the Ignacio's implementation of the Minesweeper game.

# Try it live at:

    Api:
        https://minesweeper-kriche.herokuapp.com/minesweeper/

    Api Documentation:
        https://minesweeper-kriche.herokuapp.com/minesweeper/v2/api-docs

    Swagger UI:
        https://minesweeper-kriche.herokuapp.com/minesweeper/swagger-ui/

    Usage example:
        1. create your player:
            POST https://minesweeper-kriche.herokuapp.com/minesweeper/player/{user name}
        2. create your game:
            POST https://minesweeper-kriche.herokuapp.com/minesweeper/game/{userName}​
            or custom game:
            POST https://minesweeper-kriche.herokuapp.com/minesweeper/game/{userName}​/{rows}​/{columns}​/{mines}
        3. play:
            PATCH https://minesweeper-kriche.herokuapp.com/minesweeper/game/{id}/board/{row}/{column}

    ****************************************************************************************
    *** PLEASE, allow a few seconds after the first hit for Heroku to start the service. ***
    *** Subsequent calls should be faster.                                               ***
    ****************************************************************************************

# decisions taken and important notes:

1. Please, find original content of this file at the bottom.

2. Heroku limits the DB usage (10000 records ish) for a free account, please let me know if you get SQL errors.

3. Programming Strategy:
    I decided to start with a simple design not worrying too much in packaging structure or performance. As code grows
    and more classes are added. I organize them into packages.

4. Out of scope:
    I was instructed by Daiana Vazquez ONLY to develop the Java backend side. Happy to discuss a client in React Native.
    Some versions of Minesweeper will set up the board by never placing a mine on the first square revealed.
    Avoidable guesses.
    Thread safety / concurrency.
    Security.
    Pagination.
    Stress testing.
    However, I will be happy to discuss how to implement any of these.

5. Game model:

    The cell:

    Option 1, Cell state will be 4 independents fields:
        mined: boolean.
        revealed: boolean.
        mark: flag, question, no mark.
        adjacent mines.

    Option 2, Cell state will be 3 independents fields:
        mined: boolean.
        mark: unrevealed_flag, unrevealed_question, unrevealed_no_mark, revealed.
        adjacent mines.

    After some thoughts I find option 2 closer to game rules since it makes no sense to have a revealed mine flagged.
    To prevent reaching an inconsistent state, I went with option 2. On the other hand, option 1 could be more flexible
    to new rules.
    It's also not needed to compute adjacent mines for a mined cell.

    The Board:

        a possibility is to use a Cell[][] however keeping in mind persistence (since I went with a relational DB using
        JPA/Hibernate) I decided to use List<List<Cell>>.
        If an API call results in just one cell mutated then it will trigger just SQL operations related to that cell.

    The Game:

        I keep some calculated values such as available flags and revealed cells in the Game object to avoid walking the
        whole board each time the user makes a move.

6. The API:

    for all operations that can have the game board changed, I decided to return the whole game.
    This makes sense especially when revealing a cell which can trigger many more cells revealed.

7. Validations:

    game rules validations are performed by the GameService class and latter translated to Http
    response codes at controller level thus keeping the Domain layer independent from the View layer.

8. Persistence:
    JPA with Hibernate, H2 for local, PosgreSQL for Heroku. Happy to discuss other approaches.

9. Design notes:

    There are some "player moves" such as flagging/revealing a cell that have impact not only in the cell but
    in the state of the game. I unified the logic to handle this under "one single entry point" to reduce the
    possibility of introducing bugs.
    Check: GameService.markCellAndUpdateGameCounters.
    Another example is time tracking: when the game state changes from/to pause/resume the time has to be
    tracked so I added that logic inside Game.setState method to make it harder to make a mistake there.

    There is one class intentionally @Deprecated to highlight its implementation is not suitable for a real case:
    RandomService

10. Because of the Cell JSON model described in point 5 when the game finishes there is no need to reveal all the cells.
    The client will have all the information needed in the board to display to the user the complete picture.
    For example a mined cell will now contain all the fields:
    {
            "mined": true,
            "adjacentMines": 0,
            "state": "UNREVEALED_QUESTION_MARK"
    }

    {
            "mined": true,
            "adjacentMines": 0,
            "state": "REVEALED" --> this was the cell that caused the user to loose.
    }


11. Known issues (should I be telling you this?):
    Persistence is using the same default sequence across all tables. Ideally there should be one per table.


Original content follows:

# minesweeper-API
API test

We ask that you complete the following challenge to evaluate your development skills. Please use the programming language and framework discussed during your interview to accomplish the following task.

PLEASE DO NOT FORK THE REPOSITORY. WE NEED A PUBLIC REPOSITORY FOR THE REVIEW. 

## The Game
Develop the classic game of [Minesweeper](https://en.wikipedia.org/wiki/Minesweeper_(video_game))

## Show your work

1.  Create a Public repository ( please dont make a pull request, clone the private repository and create a new plublic one on your profile)
2.  Commit each step of your process so we can follow your thought process.

## What to build
The following is a list of items (prioritized from most important to least important) we wish to see:
* Design and implement  a documented RESTful API for the game (think of a mobile app for your API)
* Implement an API client library for the API designed above. Ideally, in a different language, of your preference, to the one used for the API
* When a cell with no adjacent mines is revealed, all adjacent squares will be revealed (and repeat)
* Ability to 'flag' a cell with a question mark or red flag
* Detect when game is over
* Persistence
* Time tracking
* Ability to start a new game and preserve/resume the old ones
* Ability to select the game parameters: number of rows, columns, and mines
* Ability to support multiple users/accounts
 
## Deliverables we expect:
* URL where the game can be accessed and played (use any platform of your preference: heroku.com, aws.amazon.com, etc)
* Code in a public Github repo
* README file with the decisions taken and important notes

## Time Spent
You need to fully complete the challenge. We suggest not spending more than 5 days total.  Please make commits as often as possible so we can see the time you spent and please do not make one commit.  We will evaluate the code and time spent.
 
What we want to see is how well you handle yourself given the time you spend on the problem, how you think, and how you prioritize when time is sufficient to solve everything.

Please email your solution as soon as you have completed the challenge or the time is up.
