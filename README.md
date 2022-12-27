# lukefeilberg.me
This is a work in progress site where I intend to upload miscallaneous projects, mostly related to math, data, AI types of stuff.

## How it works 👩‍💻
I write markdown files and convert them to HTML with `convert.py` and plop that into a basic template HTML page.

## Basic Specs 👓
- Base/Template HTML
	- Uses [Bootstrap](https://getbootstrap.com/docs/3.4/) CSS for a nice Navigation bar and generally seems to make everything work a bit better across mobile and desktop
	- [Pygment](https://pygments.org/)'s code highlight
	- [MathJax](https://www.mathjax.org/) for Latex
	- Some extra custom styling in my own CSS file
- I then use [Python Markdown](https://python-markdown.github.io/) to convert my Markdown files into HTML
	- I use the [Fenced Code Blocks](https://python-markdown.github.io/extensions/fenced_code_blocks/) extension so I can use triple tick code blocks that are much preferable in my opinion to indentation based code blocks
	- And I use the [CodeHilite](https://python-markdown.github.io/extensions/code_hilite/) extension to add code/syntax highlighting
- The HTML generated from Python Markdown is then plopped into my base HTML page using [jinja2](https://palletsprojects.com/p/jinja/) 
	- Frankly, using jinja is a bit overkill for now since I'm currently only plopping the body HTML into the base but it may be nice to better utilize in the future
