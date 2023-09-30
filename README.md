# NHL-Predictor
A program that implements machine learning and data analysis to predict the outcome of an NHL game.

Currently at about 60% precision score.

The project is not yet completely put together. Currently, the Java side of the project is outputting the necessary CSVs, where the Python script will analyze those CSVs and handle the machine learning. 

The next step for the project is to combine these. Our Java project will automatically call the Python script, record its output, and use it for the overall program, which will compile match-up stats and predictions for a given game. It will also check the CSVs and update them with the most recent game data if needed.
