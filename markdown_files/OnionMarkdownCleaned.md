# The Onion Classifier 🧅

[The Onion](https://www.theonion.com) is Americas's Finest News Source -- a fantastic site of satirical news headlines. However, non-satirical news outlets seem to often compete with The Onion often blurring the lines on who is actually more absurd. Thus, a subreddit called [r/NotTheOnion](https://www.reddit.com/r/nottheonion) was born to collect headlines from supposedly real news stories that could be mistaken for Onion headlines. The subreddit is quite popular with about 22 million members and chalk full of headlines that really can be tough to distinguish from satire.

So, this is supposedly a difficult classification problem for humans which begs the question -- *Can a machine learning model do better?* 😉

```python
import pandas as pd
import numpy as np
import re
import time
import requests
import json
import datetime
import tensorflow as tf
```

## Part 1: Getting Data 📜
Data is extracted from Reddit using the Pushshift API as documented here: [github.com/pushshift/api](https://github.com/pushshift/api)

### Getting dates to extract data between
The Pushshift API only returns at most 1000 posts with each request, so I create a list of dates to pull ~1000 posts between these dates.


```python
dates_list = []

# Making list of dates; Each January 1st and June 1st from 2015 until January 1st 2020
for i in range(10,21):
    dates_list.append('01/01/20'+str(i))
    dates_list.append('01/06/20'+str(i))

# Popping June 2020 since it hasn't happened yet
dates_list.pop()
dates_list
```



<div class="outputcode">[
  '01/01/2010',
  '01/06/2010',
  '01/01/2011',
  '01/06/2011',
  '01/01/2012',
  '01/06/2012',
  '01/01/2013',
  '01/06/2013',
  '01/01/2014',
  '01/06/2014',
  '01/01/2015',
  '01/06/2015',
  '01/01/2016',
  '01/06/2016',
  '01/01/2017',
  '01/06/2017',
  '01/01/2018',
  '01/06/2018',
  '01/01/2019',
  '01/06/2019',
  '01/01/2020'
]</div>


### Converting dates to Unix timestamp
Returns Unix timestamp that Pushshift API requires for dates


```python
def getTimeStamp(date_input):
    return time.mktime(datetime.datetime.strptime(date_input, "%d/%m/%Y").timetuple())

dates = [int(getTimeStamp(date)) for date in dates_list]
dates
```



<div class="outputcode">[
  1262332800,
  1275375600,
  1293868800,
  1306911600,
  1325404800,
  1338534000,
  1357027200,
  1370070000,
  1388563200,
  1401606000,
  1420099200,
  1433142000,
  1451635200,
  1464764400,
  1483257600,
  1496300400,
  1514793600,
  1527836400,
  1546329600,
  1559372400,
  1577865600
]</div>


### Getting Pushshift data
Returns the top 1000 posts in the given subreddit between the given times.  
Code modified from the following article:  
[medium.com/@RareLoot/using-pushshifts-api-to-extract-reddit-submissions-fb517b286563](https://medium.com/@RareLoot/using-pushshifts-api-to-extract-reddit-submissions-fb517b286563)


```python
def getPushshiftData(after, before, sub):
    url = ('https://api.pushshift.io/reddit/search/submission/?size=1000&after='+
           str(after)+'&before='+str(before)+'&subreddit='+str(sub)+'&sort_type=score'+'&sort=desc')
    print(url)
    r = requests.get(url)
    data = json.loads(r.text)
    return data['data']
```

### Getting all the titles between the dates chosen earlier
Here I loop through all the dates above and get the top ~1000 posts in the chosen subreddit.   

I end up with 9065 Onion headlines and 15432 "fake"-Onion headlines from r/NotThenOnion.  
I then keep the first 9000 and first 15000 for easier batching. 


```python
def getTitles(subreddit):
    titles_new = []
    titles = []

    for i in range(len(dates)-1):
        # Setting up dates
        after  = dates[i]
        before = dates[i+1]

        # Getting subreddit data between the dates after and before from r/NotTheOnion
        raw_json = getPushshiftData(after,before,subreddit)

        # Extracting just the title
        titles_new = [post['title'] for post in raw_json]

        # Appending new data on
        titles = titles + titles_new

    # A few posts were extracted twice, set gets rid of duplicates
    titles = list(set(titles))
    return titles

not_onion = getTitles('nottheonion')
onion = getTitles('theonion')

onion = onion[:9000]
not_onion = not_onion[:15000]
```
<div class="outputcode">https://api.pushshift.io/reddit/search/submission/?size=1000&after=1262332800&before=1275375600&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1275375600&before=1293868800&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1293868800&before=1306911600&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1306911600&before=1325404800&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1325404800&before=1338534000&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1338534000&before=1357027200&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1357027200&before=1370070000&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1370070000&before=1388563200&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1388563200&before=1401606000&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1401606000&before=1420099200&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1420099200&before=1433142000&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1433142000&before=1451635200&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1451635200&before=1464764400&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1464764400&before=1483257600&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1483257600&before=1496300400&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1496300400&before=1514793600&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1514793600&before=1527836400&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1527836400&before=1546329600&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1546329600&before=1559372400&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1559372400&before=1577865600&subreddit=nottheonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1262332800&before=1275375600&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1275375600&before=1293868800&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1293868800&before=1306911600&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1306911600&before=1325404800&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1325404800&before=1338534000&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1338534000&before=1357027200&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1357027200&before=1370070000&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1370070000&before=1388563200&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1388563200&before=1401606000&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1401606000&before=1420099200&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1420099200&before=1433142000&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1433142000&before=1451635200&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1451635200&before=1464764400&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1464764400&before=1483257600&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1483257600&before=1496300400&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1496300400&before=1514793600&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1514793600&before=1527836400&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1527836400&before=1546329600&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1546329600&before=1559372400&subreddit=theonion&sort_type=score&sort=desc
https://api.pushshift.io/reddit/search/submission/?size=1000&after=1559372400&before=1577865600&subreddit=theonion&sort_type=score&sort=desc
</div>

### Converting to pandas dataframe
Labeling Onion headlines as 1, and r/NotTheOnion headlines as 0.


```python
df1= pd.DataFrame({'text':onion})
df1['label'] = 1

df2 = pd.DataFrame({'text':not_onion})
df2['label'] = 0

# Combining both datasets
df = pd.concat([df1,df2])

# Shuffling the dataset
df = df.sample(frac=1).reset_index(drop=True)

# Converting all text to lowercase, fixing ampersands and getting rid
# of dashes and apostrophes as they can mess up the dictionary
df['text'] = df['text'].str.lower()
df['text'] = df['text'].str.replace(r'&amp;', 'and')
df['text'] = df['text'].str.replace(r'-', ' ')
df['text'] = df['text'].str.replace(r'[^\s\w]','')

# Saving the dataframe to a csv file
df.to_csv('OnionOrNot.csv')

df.head()
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }

    .dataframe thead th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>text</th>
      <th>label</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>0</td>
      <td>egypt funeral turns happy after dead man awakes</td>
      <td>0</td>
    </tr>
    <tr>
      <td>1</td>
      <td>michael jordan attacks softness lack of compet...</td>
      <td>1</td>
    </tr>
    <tr>
      <td>2</td>
      <td>feds bust massive child pornography corporatio...</td>
      <td>1</td>
    </tr>
    <tr>
      <td>3</td>
      <td>joker arrested for flashing his penis   but it...</td>
      <td>0</td>
    </tr>
    <tr>
      <td>4</td>
      <td>mexican lawmaker wants to fine people 1600 for...</td>
      <td>0</td>
    </tr>
  </tbody>
</table>
</div>



### Reading in dataframe
Running this when I return to the project so I don't have to use the Pushshift API etc. again.


```python
df = pd.read_csv('OnionOrNot.csv', index_col = 0)
df.head()
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }

    .dataframe thead th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>text</th>
      <th>label</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>0</td>
      <td>egypt funeral turns happy after dead man awakes</td>
      <td>0</td>
    </tr>
    <tr>
      <td>1</td>
      <td>michael jordan attacks softness lack of compet...</td>
      <td>1</td>
    </tr>
    <tr>
      <td>2</td>
      <td>feds bust massive child pornography corporatio...</td>
      <td>1</td>
    </tr>
    <tr>
      <td>3</td>
      <td>joker arrested for flashing his penis   but it...</td>
      <td>0</td>
    </tr>
    <tr>
      <td>4</td>
      <td>mexican lawmaker wants to fine people 1600 for...</td>
      <td>0</td>
    </tr>
  </tbody>
</table>
</div>



## Part 2: Encoding Words as Numbers 🔢

### Getting all the words in the training data


```python
vocab_set = set()
sentence_lengths = []

for i in range(len(df)):
    # Updates adds all items to the set, re.split splits the text into words
    sentence_words = re.split(r'\s',df.iloc[i]['text'])
    vocab_set.update(sentence_words)
    sentence_lengths.append(len(sentence_words))
```

### Converting the words to a dictionary
This way we can map the words in the dataframe to lists of numbers


```python
vocab_list = list(vocab_set)
vocab_dict = {vocab_list[i-1]: i for i in range(1, len(vocab_list)+1)}
```

### Creating column of the words mapped to numbers


```python
max_length = max(sentence_lengths)

def toNumbers(row):
    words = re.findall(r'([\w]+)', row['text'])
    nums =  np.array([vocab_dict[words[j]] for j in range(len(words))])
    return np.pad(nums, (0, max_length - len(nums)), mode='constant')
```
<br>

```python
nums = df.apply(lambda row: toNumbers(row), axis=1) 
df['nums'] = nums

df['nums'].head()
```



<div class="outputcode">    0    [10101, 15701, 24365, 6689, 22221, 4330, 4928,...
    1    [6556, 7335, 1523, 21250, 6690, 23567, 18468, ...
    2    [20493, 17894, 4253, 9925, 21346, 24068, 7515,...
    3    [18219, 15505, 9902, 24892, 16634, 10504, 810,...
    4    [16068, 3826, 16392, 14837, 1613, 5793, 15082,...
    Name: nums, dtype: object
</div>


### Converting to Numpy arrays


```python
labels = np.asarray(df['label'].values)
features = np.stack(df['nums'].values)

features.shape, labels.shape
```



<div class="outputcode">((24000, 64), (24000,))
</div>


## Part 3: Building and Fitting the Model 🔬


```python
def get_compiled_model():
    embedding_dim=16

    model = tf.keras.Sequential([
        tf.keras.layers.Embedding(len(vocab_set)+1, 64),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64,  return_sequences=True)),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(32)),
        tf.keras.layers.Dense(64, activation='relu'),
        tf.keras.layers.Dropout(0.5),
        tf.keras.layers.Dense(1, activation='sigmoid')
    ])


    model.compile(optimizer='adam',
                loss='binary_crossentropy',
                metrics=['accuracy'])
    
    return model

model = get_compiled_model()
model.fit(features, labels, batch_size=32, epochs=5, verbose=2, validation_split=0.2);
```
<div class="outputcode">Train on 19200 samples, validate on 4800 samples
    Epoch 1/5
    19200/19200 - 173s - loss: 0.4103 - accuracy: 0.8130 - val_loss: 0.3051 - val_accuracy: 0.8715
    Epoch 2/5
    19200/19200 - 152s - loss: 0.1713 - accuracy: 0.9377 - val_loss: 0.3448 - val_accuracy: 0.8652
    Epoch 3/5
    19200/19200 - 116s - loss: 0.0685 - accuracy: 0.9778 - val_loss: 0.4366 - val_accuracy: 0.8554
    Epoch 4/5
    19200/19200 - 121s - loss: 0.0348 - accuracy: 0.9891 - val_loss: 0.6293 - val_accuracy: 0.8344
    Epoch 5/5
    19200/19200 - 127s - loss: 0.0242 - accuracy: 0.9923 - val_loss: 0.6790 - val_accuracy: 0.8442
</div> 

### Tuning parameters, modifying model, etc.

#### Trial 1: ~85% validation accuracy around epoch 5
```python
embedding_dim=16

model = keras.Sequential([
  layers.Embedding(len(vocab_set)+1, embedding_dim),
  layers.GlobalAveragePooling1D(),
  layers.Dense(16, activation='relu'),
  layers.Dense(1, activation='sigmoid')
])
```

#### Trial 2: ~85% validation accuracy around epoch 5  
No discernable change in accuracy tuning embedding_dim 
```python
embedding_dim=32

model = keras.Sequential([
  layers.Embedding(len(vocab_set)+1, embedding_dim),
  layers.GlobalAveragePooling1D(),
  layers.Dense(16, activation='relu'),
  layers.Dense(1, activation='sigmoid')
])
```

#### Trial 3: ~87% validation accuracy on epoch 1
Starts overfitting after epoch 1
```python
embedding_dim=16

model = tf.keras.Sequential([
    tf.keras.layers.Embedding(len(vocab_set)+1, 64),
    tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64)),
    tf.keras.layers.Dense(64, activation='relu'),
    tf.keras.layers.Dense(1, activation='sigmoid')
])
```

#### Trial 4: ~87% validation accuracy on epoch 1
Again starts overfitting after epoch 1
```python
embedding_dim=16

model = tf.keras.Sequential([
    tf.keras.layers.Embedding(len(vocab_set)+1, 64),
    tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64,  return_sequences=True)),
    tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(32)),
    tf.keras.layers.Dense(64, activation='relu'),
    tf.keras.layers.Dropout(0.5),
    tf.keras.layers.Dense(1, activation='sigmoid')
])
```

## Part 4: Conclusion 🤖
So, playing around with some simple models and parameters it appears we can get about 87% validation accuracy. I haven't done a true deep dive but after shuffling the "real" vs "fake" headlines and investigating myself I seem to be about that accurate, perhaps a bit more!

You can download the CSV of all the data in my [GitHub repo](https://github.com/lukefeilberg/onion) for the project (or of course follow along above to get the data yourself!). If you check it out let me know if you try any models and beat my "benchmarks" above 😜. You can always reach me by the email in the footer!
