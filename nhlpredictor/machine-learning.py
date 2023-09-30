import pandas as pd
import csv
from sklearn.metrics import precision_score
from sklearn.ensemble import RandomForestClassifier

matches = pd.read_csv("output.csv", index_col=0, header=0, engine="python", skip_blank_lines=True, on_bad_lines='skip')
matches["Date"] = pd.to_datetime(matches["Date"], format="mixed")
matches["Location_code"] = matches["Location"].astype("category").cat.codes

matches = matches.dropna()

test = pd.read_csv("test.csv", index_col=0, header=0, engine="python", skip_blank_lines=True, on_bad_lines='skip')
test["Date"] = pd.to_datetime(test["Date"], format="mixed")
test["Location_code"] = test["Location"].astype("category").cat.codes

test = test.dropna()

rf = RandomForestClassifier(n_estimators=200, min_samples_split=100, random_state=1)


train = matches

predictors = ["Location_code", "OpponentID", "Point Percentage", "Shots Allowed / Game",
				"Shots / Game", "Faceoff Win Percentage", "Goals Against / Game", "Goals / Game", "Save Percentage" ]

rf.fit(train[predictors], train["Outcome"])

preds = rf.predict(test[predictors])

error = precision_score(test["Outcome"], preds)
print(error)