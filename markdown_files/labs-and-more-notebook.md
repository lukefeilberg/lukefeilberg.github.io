# Dogs for Adoption Scrape and Text! 🐶

The website [labsandmore.org/dogs](https://labsandmore.org/dogs/) uploads dogs in California that are up for adoption. The problem is the waitlist fills up extremely fast and there's no notification system or email list.

Thus I've been called to duty to scrape the website every hour and send a text when new dogs have been found for my friend looking to adopt! I use the Selenium package to scrape the website, Pandas for some of the minor data wrangling, and finally Yagmail to send the text (from email).

## Importing packages 📦


```python
import pandas as pd
import yagmail
import time
import re

from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
```

## Using Selenium to open website 🕸


```python
# Setting path and opening browser
path = r'C:\Users\lukef\AppData\Local\BrowserDriver\geckodriver.exe'

# Setting headless
options = webdriver.FirefoxOptions()
options.headless = True

driver = webdriver.Firefox(executable_path = path, options = options)

# Navigating to website
driver.get('https://labsandmore.org/dogs/')
```

## Scraping the breed, details, and names ℹ


```python
i = 0
breed = []
details = []
name = []


# First looping through breed and details elements
for li in driver.find_elements_by_xpath("//div[@class='property-stats']//ul//li"):
       
   
    # Alternates between breed and details in the li (list) elements
    if i % 2 == 0:
        breed.append(li.text)
    else:
        details.append(li.text)
    
    i += 1
    
# Now looping through the name elements
for li in driver.find_elements_by_xpath("//div[@class='tile-footer']/div[@class='price']/a"):
       
    name.append(li.text)


driver.close()
```

## Creating pandas dataframe 📈


```python
df = pd.DataFrame(dict(name=name,breed=breed,details=details))
df
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
      <th>name</th>
      <th>breed</th>
      <th>details</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>America</td>
      <td>Shepherd (mix)</td>
      <td>Female, 12 years, 46 lbs. ID 7874</td>
    </tr>
    <tr>
      <th>1</th>
      <td>Artichoke</td>
      <td>Husky (mix)</td>
      <td>Female, 2 years, 60 lbs. ID 11658</td>
    </tr>
    <tr>
      <th>2</th>
      <td>Artichoke Pup - Broccoli</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Female, 7 weeks, 5 lbs. ID 11659</td>
    </tr>
    <tr>
      <th>3</th>
      <td>Artichoke Pup - Celery</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Male, 7 weeks, 5 lbs. ID 11660</td>
    </tr>
    <tr>
      <th>4</th>
      <td>Artichoke Pup - Cucumber</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Male, 7 weeks, 5 lbs. ID 11661</td>
    </tr>
    <tr>
      <th>...</th>
      <td>...</td>
      <td>...</td>
      <td>...</td>
    </tr>
    <tr>
      <th>155</th>
      <td>Yoda</td>
      <td>Labrador Retriever - American Staffordshire Te...</td>
      <td>Male, 3 years, 54 lbs. ID 11751</td>
    </tr>
    <tr>
      <th>156</th>
      <td>Yoko - Adopted!</td>
      <td>Boxer (purebred)</td>
      <td>Female, 2 years, 63 lbs. ID 11365</td>
    </tr>
    <tr>
      <th>157</th>
      <td>Zinc</td>
      <td>Terrier - Pointer (mix)</td>
      <td>Female, 2 years, 32 lbs. ID 11531</td>
    </tr>
    <tr>
      <th>158</th>
      <td>Zipper - Adopted!</td>
      <td>Labrador Retriever - Cattle Dog (mix)</td>
      <td>Female, 2 years, 40 lbs. ID 11489</td>
    </tr>
    <tr>
      <th>159</th>
      <td>Zippy - Adopted!</td>
      <td>Shepherd - Labrador Retriever (mix)</td>
      <td>Male, 5 months, 22 lbs. ID 11794</td>
    </tr>
  </tbody>
</table>
<p>160 rows × 3 columns</p>
</div>



## Extracting the info from the details column 🐩


```python
df[['gender','age','weight','id']] = df['details'].str.extract(pat = r'(.*?),\s(.*?),\s(.*?)\.\sID\s(\d*)')

df.drop(columns = 'details', inplace = True)

df['id'] = df['id'].astype('int32')

df
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
      <th>name</th>
      <th>breed</th>
      <th>gender</th>
      <th>age</th>
      <th>weight</th>
      <th>id</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>America</td>
      <td>Shepherd (mix)</td>
      <td>Female</td>
      <td>12 years</td>
      <td>46 lbs</td>
      <td>7874</td>
    </tr>
    <tr>
      <th>1</th>
      <td>Artichoke</td>
      <td>Husky (mix)</td>
      <td>Female</td>
      <td>2 years</td>
      <td>60 lbs</td>
      <td>11658</td>
    </tr>
    <tr>
      <th>2</th>
      <td>Artichoke Pup - Broccoli</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Female</td>
      <td>7 weeks</td>
      <td>5 lbs</td>
      <td>11659</td>
    </tr>
    <tr>
      <th>3</th>
      <td>Artichoke Pup - Celery</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Male</td>
      <td>7 weeks</td>
      <td>5 lbs</td>
      <td>11660</td>
    </tr>
    <tr>
      <th>4</th>
      <td>Artichoke Pup - Cucumber</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Male</td>
      <td>7 weeks</td>
      <td>5 lbs</td>
      <td>11661</td>
    </tr>
    <tr>
      <th>...</th>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
    </tr>
    <tr>
      <th>155</th>
      <td>Yoda</td>
      <td>Labrador Retriever - American Staffordshire Te...</td>
      <td>Male</td>
      <td>3 years</td>
      <td>54 lbs</td>
      <td>11751</td>
    </tr>
    <tr>
      <th>156</th>
      <td>Yoko - Adopted!</td>
      <td>Boxer (purebred)</td>
      <td>Female</td>
      <td>2 years</td>
      <td>63 lbs</td>
      <td>11365</td>
    </tr>
    <tr>
      <th>157</th>
      <td>Zinc</td>
      <td>Terrier - Pointer (mix)</td>
      <td>Female</td>
      <td>2 years</td>
      <td>32 lbs</td>
      <td>11531</td>
    </tr>
    <tr>
      <th>158</th>
      <td>Zipper - Adopted!</td>
      <td>Labrador Retriever - Cattle Dog (mix)</td>
      <td>Female</td>
      <td>2 years</td>
      <td>40 lbs</td>
      <td>11489</td>
    </tr>
    <tr>
      <th>159</th>
      <td>Zippy - Adopted!</td>
      <td>Shepherd - Labrador Retriever (mix)</td>
      <td>Male</td>
      <td>5 months</td>
      <td>22 lbs</td>
      <td>11794</td>
    </tr>
  </tbody>
</table>
<p>160 rows × 6 columns</p>
</div>



## Checking if dog is unavailable ❌
i.e. already adopted or the waiting list is full.


```python
df['unavailable'] = df['name'].str.lower().str.contains('adopted') \
                  | df['name'].str.lower().str.contains('list full')
df
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
      <th>name</th>
      <th>breed</th>
      <th>gender</th>
      <th>age</th>
      <th>weight</th>
      <th>id</th>
      <th>unavailable</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>America</td>
      <td>Shepherd (mix)</td>
      <td>Female</td>
      <td>12 years</td>
      <td>46 lbs</td>
      <td>7874</td>
      <td>False</td>
    </tr>
    <tr>
      <th>1</th>
      <td>Artichoke</td>
      <td>Husky (mix)</td>
      <td>Female</td>
      <td>2 years</td>
      <td>60 lbs</td>
      <td>11658</td>
      <td>False</td>
    </tr>
    <tr>
      <th>2</th>
      <td>Artichoke Pup - Broccoli</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Female</td>
      <td>7 weeks</td>
      <td>5 lbs</td>
      <td>11659</td>
      <td>False</td>
    </tr>
    <tr>
      <th>3</th>
      <td>Artichoke Pup - Celery</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Male</td>
      <td>7 weeks</td>
      <td>5 lbs</td>
      <td>11660</td>
      <td>False</td>
    </tr>
    <tr>
      <th>4</th>
      <td>Artichoke Pup - Cucumber</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Male</td>
      <td>7 weeks</td>
      <td>5 lbs</td>
      <td>11661</td>
      <td>False</td>
    </tr>
    <tr>
      <th>...</th>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
    </tr>
    <tr>
      <th>155</th>
      <td>Yoda</td>
      <td>Labrador Retriever - American Staffordshire Te...</td>
      <td>Male</td>
      <td>3 years</td>
      <td>54 lbs</td>
      <td>11751</td>
      <td>False</td>
    </tr>
    <tr>
      <th>156</th>
      <td>Yoko - Adopted!</td>
      <td>Boxer (purebred)</td>
      <td>Female</td>
      <td>2 years</td>
      <td>63 lbs</td>
      <td>11365</td>
      <td>True</td>
    </tr>
    <tr>
      <th>157</th>
      <td>Zinc</td>
      <td>Terrier - Pointer (mix)</td>
      <td>Female</td>
      <td>2 years</td>
      <td>32 lbs</td>
      <td>11531</td>
      <td>False</td>
    </tr>
    <tr>
      <th>158</th>
      <td>Zipper - Adopted!</td>
      <td>Labrador Retriever - Cattle Dog (mix)</td>
      <td>Female</td>
      <td>2 years</td>
      <td>40 lbs</td>
      <td>11489</td>
      <td>True</td>
    </tr>
    <tr>
      <th>159</th>
      <td>Zippy - Adopted!</td>
      <td>Shepherd - Labrador Retriever (mix)</td>
      <td>Male</td>
      <td>5 months</td>
      <td>22 lbs</td>
      <td>11794</td>
      <td>True</td>
    </tr>
  </tbody>
</table>
<p>160 rows × 7 columns</p>
</div>



## Reading in saved (old) dataframe 📉


```python
try:
    old = pd.read_csv('dogs.csv')
except:
    old = pd.DataFrame(columns = df.columns)
    
old
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
      <th>name</th>
      <th>breed</th>
      <th>gender</th>
      <th>age</th>
      <th>weight</th>
      <th>id</th>
      <th>unavailable</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>America</td>
      <td>Shepherd (mix)</td>
      <td>Female</td>
      <td>12 years</td>
      <td>46 lbs</td>
      <td>7874</td>
      <td>False</td>
    </tr>
    <tr>
      <th>1</th>
      <td>Asteroid - (Medical)</td>
      <td>Labrador Retriever - Cattle Dog (mix)</td>
      <td>Female</td>
      <td>3 years</td>
      <td>28 lbs</td>
      <td>11632</td>
      <td>False</td>
    </tr>
    <tr>
      <th>2</th>
      <td>Barcelona - Adopted!</td>
      <td>Labrador Retriever - Shepherd (mix)</td>
      <td>Female</td>
      <td>4 months</td>
      <td>15 lbs</td>
      <td>11636</td>
      <td>True</td>
    </tr>
    <tr>
      <th>3</th>
      <td>Barnes - Waiting List Full</td>
      <td>Labrador Retriever - Shepherd (mix)</td>
      <td>Female</td>
      <td>5 months</td>
      <td>10 lbs</td>
      <td>11474</td>
      <td>True</td>
    </tr>
    <tr>
      <th>4</th>
      <td>Bayleigh</td>
      <td>Labrador Retriever (mix)</td>
      <td>Female</td>
      <td>4 years</td>
      <td>47 lbs</td>
      <td>11547</td>
      <td>False</td>
    </tr>
    <tr>
      <th>...</th>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
    </tr>
    <tr>
      <th>209</th>
      <td>NaN</td>
      <td>Shepherd (mix)</td>
      <td>Female</td>
      <td>1 year</td>
      <td>42 lbs</td>
      <td>11410</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>210</th>
      <td>NaN</td>
      <td>Labrador Retriever - Terrier (mix)</td>
      <td>Female</td>
      <td>5 years</td>
      <td>30 lbs</td>
      <td>10888</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>211</th>
      <td>NaN</td>
      <td>Labrador Retriever (mix)</td>
      <td>Female</td>
      <td>2 years</td>
      <td>40 lbs</td>
      <td>11409</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>212</th>
      <td>NaN</td>
      <td>Labrador Retriever (mix)</td>
      <td>Male</td>
      <td>1 year</td>
      <td>60 lbs</td>
      <td>11282</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>213</th>
      <td>NaN</td>
      <td>Labrador Retriever - Terrier (mix)</td>
      <td>Female</td>
      <td>1 year</td>
      <td>35 lbs</td>
      <td>11439</td>
      <td>NaN</td>
    </tr>
  </tbody>
</table>
<p>214 rows x 7 columns</p>
</div>



## Send email function for if new dogs are found 📧


```python
def send_email(df):
    sender_email = 'lukefeilbergp@gmail.com'
    receiver_email = 'enter_phone_number_here@tmomail.net'
    subject = "New dogs alert!"
    password = 'Enter_Password_Here'
    
    contents = []
    
    yag = yagmail.SMTP(user=sender_email, password=password)
    
    # Displaying at most 5
    for i in range(min(len(df), 5)):
        contents.append('')
        contents.append(df.iloc[i]['name']
                        + ', '
                        + df.iloc[i]['breed']
                        + ', '
                        + df.iloc[i]['age'])
    
    contents.extend(['', 'https://labsandmore.org/dogs/'])

    yag.send(receiver_email, subject, contents);
```

## Checking for new dogs, sending email if so! 🐕


```python
if sum(~df['id'].isin(old['id'])) > 0:
    print('New dogs found! :-)')
    
    new_dogs = df[~df['id'].isin(old['id'])]
    
    send_email(new_dogs)
```

    New dogs found! :-)
    

## Concatenating and dropping duplicates ♊


```python
old_and_new = pd.concat([df, old])  

old_and_new.drop_duplicates(subset='id',keep='first',inplace=True)

old_and_new
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
      <th>name</th>
      <th>breed</th>
      <th>gender</th>
      <th>age</th>
      <th>weight</th>
      <th>id</th>
      <th>unavailable</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>America</td>
      <td>Shepherd (mix)</td>
      <td>Female</td>
      <td>12 years</td>
      <td>46 lbs</td>
      <td>7874</td>
      <td>False</td>
    </tr>
    <tr>
      <th>1</th>
      <td>Artichoke</td>
      <td>Husky (mix)</td>
      <td>Female</td>
      <td>2 years</td>
      <td>60 lbs</td>
      <td>11658</td>
      <td>False</td>
    </tr>
    <tr>
      <th>2</th>
      <td>Artichoke Pup - Broccoli</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Female</td>
      <td>7 weeks</td>
      <td>5 lbs</td>
      <td>11659</td>
      <td>False</td>
    </tr>
    <tr>
      <th>3</th>
      <td>Artichoke Pup - Celery</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Male</td>
      <td>7 weeks</td>
      <td>5 lbs</td>
      <td>11660</td>
      <td>False</td>
    </tr>
    <tr>
      <th>4</th>
      <td>Artichoke Pup - Cucumber</td>
      <td>Husky - Shepherd (mix)</td>
      <td>Male</td>
      <td>7 weeks</td>
      <td>5 lbs</td>
      <td>11661</td>
      <td>False</td>
    </tr>
    <tr>
      <th>...</th>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
    </tr>
    <tr>
      <th>209</th>
      <td>NaN</td>
      <td>Shepherd (mix)</td>
      <td>Female</td>
      <td>1 year</td>
      <td>42 lbs</td>
      <td>11410</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>210</th>
      <td>NaN</td>
      <td>Labrador Retriever - Terrier (mix)</td>
      <td>Female</td>
      <td>5 years</td>
      <td>30 lbs</td>
      <td>10888</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>211</th>
      <td>NaN</td>
      <td>Labrador Retriever (mix)</td>
      <td>Female</td>
      <td>2 years</td>
      <td>40 lbs</td>
      <td>11409</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>212</th>
      <td>NaN</td>
      <td>Labrador Retriever (mix)</td>
      <td>Male</td>
      <td>1 year</td>
      <td>60 lbs</td>
      <td>11282</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>213</th>
      <td>NaN</td>
      <td>Labrador Retriever - Terrier (mix)</td>
      <td>Female</td>
      <td>1 year</td>
      <td>35 lbs</td>
      <td>11439</td>
      <td>NaN</td>
    </tr>
  </tbody>
</table>
<p>327 rows × 7 columns</p>
</div>



## Saving to csv for next time 🛫


```python
old_and_new.to_csv('dogs.csv', index = False)
```

## Sending Texts Every Hour ⏰

The Jupyter Notebook (embedded all above) was used to build up the script from which I then essentially just copy-pasted over the cells to an actual .py file ([labs_scrape_and_text.py](https://github.com/lukefeilberg/dog_adoption_scrape/blob/master/labs_scrape_and_text.py)). I then created a [batch script](https://github.com/lukefeilberg/dog_adoption_scrape/blob/master/labs_bat.bat) which runs the python file and finally created a Task on Windows Task Scheduler that runs the batch script (which again runs the Python script) every hour every day from 10am to 8pm (only sending texts when new dogs are found). You can check out the [Github repo](https://github.com/lukefeilberg/dog_adoption_scrape) to see it all in one place!

An example text is shown below:
<div class="picture">
  <img src="https://camo.githubusercontent.com/605144d44f71f57d1ebdc0e454be4b0e50bf94440e32757b0135af77f09ea80b/68747470733a2f2f692e6962622e636f2f48433942464d6b2f546578742d53637265656e73686f742e706e67">
</div>
*Note: The Husky "Artichoke" had many pups all uploaded at the same time in case that data looked off to you!*