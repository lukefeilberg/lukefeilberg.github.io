# Boggle 🔠

<div class="boggle-input":>
  Enter number of rows: 
    <div class="boggle-input-row-col">
      <input type="text" id="rows" value="10">
    </div>
  Enter number of columns: 
    <div class="boggle-input-row-col">
      <input type="text" id="cols" value="10">
    </div>
</div>

<div class="boggle-generate-button">
  <button onclick="generateAndSolveBoggle()">Generate Board!</button>
</div>

<div class="boggle-board">
  <p id="board" style="font-family:courier;font-size:150%;"></p>
</div>

<div class = "boggle-words">
  <p id="words"></p>
</div>


<script>
async function generateAndSolveBoggle() {
    var rows = document.getElementById("rows").value;
    var cols = document.getElementById("cols").value;
    
    //GENERATING BOARD:
    var letters = "abcdefghijklmnopqrstuvwxyz" //Maybe switch to ['a',...,'qu',...,'z']
    var board = []
    for (var i = 0; i < rows; i++) {
      board[i] = [];

      for (var j = 0; j < cols; j++) {
        //Assigning random letters to the board
        board[i][j] = letters.charAt(Math.floor(rand() * letters.length))
      }
    }
  
    //DISPLAYING BOARD:
    document.getElementById("board").innerHTML = "";
    for (var i = 0; i < rows; i++) {
      document.getElementById("board").innerHTML += board[i].join(' ');
      document.getElementById("board").innerHTML += "<br>";
    }

	//GETTING DICTIONARY
	const library = await fetch('https://raw.githubusercontent.com/dwyl/english-words/master/words_alpha.txt', {});
	words = await library.text();
	words = words.split("\n");
	//for loop gets rid of the "\n" chars (last word is fine though)
	for (var i = 0; i < words.length - 1; i++) {
	  words[i] = words[i].substring(0, words[i].length - 1);
	}



    //ADDING DICTIONARY WORDS INTO TRIE
    var dict = new Trie();   
    for (var i = 0; i <  words.length; i++) {
      dict.add(words[i].toString());
    }

    //SOLVE BOARD
  solve(dict, board, rows, cols);  

}


// Credit: Victor Quinn - https://github.com/chancejs/chancejs/issues/232#issuecomment-182500222
function rand() {
    var arr = new Uint32Array(1);
    window.crypto.getRandomValues(arr);
    // This jazz is necessary to translate from a random integer to a floating point from 0 to 1
    return arr[0]/(0xffffffff + 1);
};

//TRIE-----------------------------------------------------------------
function Node(c) {
  this.c = c; //This node's character
  this.children = {};
  this.isWord = false;
}

function Trie() {
  this.root = new Node(null);
}


Trie.prototype.add = function(word) {
  var node = this.root; //Node to traverse each letter of given word

  for (var i = 0; i < word.length; i++) {

    if (!node.children[word[i]]) {

      node.children[word[i]] = new Node(word[i])

    } 

    node = node.children[word[i]]
    
  }

  node.isWord = true;
}


Trie.prototype.isPrefixOrWord = function(word) {
  /*
  
  Returns 0 if not prefix or word ; "qzedf" returns 0
  Returns 1 if only prefix        ; "tabl"  returns 1
  Returns 2 if word               ; "table" returns 2

  */
  
  var node = this.root; //Node to traverse each letter of given word

  for (var i = 0; i < word.length; i++) {

    if (!node.children[word[i]]) {
      return 0; //not a prefix or word
    }

    node = node.children[word[i]];//Moving along (onto next char)
  }

    if (!node.isWord) {
        return 1;
    } else {
        return 2;
    }
}
//--------------------------------------------------------------------
//PRIORITY QUEUE------------------------------------------------------

function BiggestFirstQueue() {
  this.items = [];
}

//TODO: Enqueue largest and in alphabetical;
BiggestFirstQueue.prototype.enqueue = function(element) {
  if (this.items.indexOf(element) > -1) {
    return;
  }

  var added = false;
  for (var i = 0; i < this.items.length; i++) {

    if (element.length > this.items[i].length) {
      this.items.splice(i, 0, element);
      added = true;
      break;
    }
  }

  if (!added) {
    this.items.push(element);
  }
}

//Prints words of top two longest lengths
BiggestFirstQueue.prototype.print = function() {

  longestLength = this.items[0].length;

  var i = 0;

    document.getElementById("words").innerHTML = "<b>Longest Words (length " + longestLength + "):</b> <br>";

  while (this.items[i].length == longestLength) {

    document.getElementById("words").innerHTML += this.items[i] + " ";
    i += 1;

  }

  secondLongestLength = this.items[i].length;

    document.getElementById("words").innerHTML += "<br><br><b>Second Longest Words (length " + secondLongestLength + "):</b> <br>";

  while (this.items[i].length == secondLongestLength) {

    document.getElementById("words").innerHTML += this.items[i] + " ";
    i += 1;

  }


  console.log(this.items)
}
//---------------------------------------------------------------------
//FINDING WORDS (ANSWERS) IN BOARD

function solve(dict, board, rows, cols) {

  var answers = new BiggestFirstQueue(); //words found on the board
  var traversed = new Set(); //index [i][j] added as i * rows + j, can't repeat letter/position

  //Looping through board. 
  for (var i = 0; i < rows; i++) {
      
    for (var j = 0; j < cols; j++) {

      traversed.add(i * rows + j);

      solveRec(dict, board, rows, cols, i, j, traversed, answers, board[i][j]);

      traversed.delete(i * rows + j);
    }

  }

  answers.print()

}

//word is built up from the letters recursively
function solveRec(dict, board, rows, cols, i, j, traversed, answers, word) {

  var p = 0; // 2 if word, 1 if just prefix, 0 if none

  //Looping through all adjacent positions
  for (var x = i - 1; x <= i + 1; x++) {

      for (var y = j - 1; y <= j + 1; y++) {

          //Avoiding edges and already used letter-positions
          if (x != -1 && y != -1 && x != rows && y != cols
              && !traversed.has(x * rows + y)) {

              p = dict.isPrefixOrWord(word + board[x][y]);//2 if word, 1 if just prefix, 0 if none

              if (p > 0) {

                  //Note: even if it's a word continue searching after as may be compound word
                  if (p == 2) {

                      answers.enqueue(word + board[x][y]);

                  }

                  traversed.add(x * rows + y);

                  solveRec(dict, board, rows, cols, x, y, traversed,
                          answers, word + board[x][y]);

                  traversed.delete(x * rows + y);

              }

          }

      }

  }

}

</script>


## Trie to Understand Tries 🌲

The <a href="https://en.wikipedia.org/wiki/Boggle" target="_blank">boggle</a> board above is generated with new random letters with every click of the generate board button. From there you can try to find words by starting with any letter and looking to the letters adjacent to it (up, down, left, right, and all diagonals) and continuing in this manner to form words with the only caveat that you can not repeat the same letter/position twice.

To find the longest words with Javascript, I first fetched ~479,000 English words from <a href="https://github.com/dwyl/english-words" target="_blank">github.com/dwyl/english-words</a> (which admittedly contains some pretty archaic words), then added every single word to my <a href="https://en.wikipedia.org/wiki/Trie">Trie</a> data structure.


The trie data structure is essentially just a tree where the nodes are letters and as you descend down the tree the letters form words. At each node a letter is stored as well as whether or not this additional letter now forms a word. An example Trie is shown below with the words "he", "hey", "hell", "hello", "world", "word", and "words".

<p style="margin-left: 100px"> 
<img src="https://i.ibb.co/grdRxrb/trie-pic.jpg" class="center" style="width:70%" ><br><br>
</p>

We see from above how the seven words are stored in a very space-saving manner. It also may be becoming clear how storing words in this way is helpful to find the words on a Boggle board. After adding all the words to my Trie, I added a function **`isWordOrPrefix`** that takes a string as the input and returns whether the string is a word, a prefix to a word (as in those letters begin forming a word), or returns none. For example, `"hello"` returns **`"word"`**, `"hel"` returns **`"prefix"`**, and `"qyxq"` returns **`"none"`**.

With all the words loaded into the Trie and the **`isWordOrPrefix`** function working, finding all the words contained on the Boggle board followed the same basic strategy anyone solving a word-search or Boggle board would use: Start at the top left letter, then scan the adjacent letters that together begin forming words, continuing this way until all the words have been formed from that starting position, and then starting over for every other letter on the board.


## Future Improvements 🔨

Since revamping my website I finally decided to update this page a little and implement a few things that were once in this "Future Improvements" section.

1. A big improvement is I now (finally) load all the dictionary words and add them to the trie data structure only _once_ on pageload, and then reuse it every time the button is clicked.
    -  I lazily just defined the trie data structure to be global as a cursory Google search didn't show me a simple way to pass values defined from a `window.onload` function call to a function triggered from a button's `onclick` value.
2. Pretty small, but I'm a bit of a keyboard warrior and don't love using my mouse, so as a small UX tweak I added a little `onkeydown` event to the row and column input elements to click the Generate Board button for you when you type Enter. 💁‍♀️

I still don't _really_ know JavaScript and the goal has remained to just have something that works rather than follows all best practices, so I'm sure there are definitely more improvements to be made but I will probably leave as is for the time-being.👨‍💻