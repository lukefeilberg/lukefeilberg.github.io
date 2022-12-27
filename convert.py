""""""
from datetime import datetime
from jinja2 import Environment, FileSystemLoader
import markdown
from markdown.extensions.codehilite import CodeHiliteExtension
from pathlib import Path
import re
import webbrowser

# Settings
open_browser = True
testing = False
md_files_to_convert = [Path(r"C:\Users\lukef\Documents\general\website\website-markdown-files\blog.md")]

# All markdown files in local repo
# md_files_to_convert.extend(list(Path.cwd().joinpath('markdown_files').iterdir()))

# Individual files instead:
# md_files_to_convert = [Path(r'markdown_files/boggle.md')]

# All Obsidian files
obsidian_md_files_dir = r'C:\Users\lukef\Documents\general\website\website-markdown-files'
# md_files_to_convert.extend(list(Path(obsidian_md_files_dir).iterdir()))

def process_markdown_file(md_text):
    """Takes raw markdown text and fixes a few problems I've had.
    
    1. Output from print statements get interpretted as Python code since
       they're indented with 4 spaces, so removing those 4 spaces.

    2. When a DataFrame is displayed the row x col dimensions are as well and I 
       want to add a special div tag to stylize this output a bit differently.
    """

    # 1. Removing 4 spaces of code output (that then get's interpretted as code)
    indented_code_output_matches = list(re.finditer(r'```\n\n    [\s\S]*?\n#', md_text))

    for match in reversed(indented_code_output_matches):
        md_text = (
            md_text[:match.start()] + 
            match.group().replace('    ', '') + 
            md_text[match.end():]
        )

    # 2. Adding special div class to DataFrame dimensions output
    row_col_dims_matches = list(re.finditer(r'\d+ rows . \d+ columns', md_text))

    for match in reversed(row_col_dims_matches):
        md_text = (
            md_text[:match.start()] +
            '<div class="dataframe-dimensions">' + match.group() + '</div>' + 
            md_text[match.end():]
        )

    return md_text


# Setting up environment and base template
env = Environment(loader=FileSystemLoader('templates'))
template = env.get_template('base.html')

# Read in markdown file, convert to HTML, add as body to base template
for md_file in md_files_to_convert:
    with open(md_file, 'r', encoding='utf-8') as f:
        md_text = f.read()

        md_text = process_markdown_file(md_text)

        html_body = markdown.markdown(
            md_text, 
            extensions=['fenced_code', CodeHiliteExtension(pygments_style='monokai'), 'pymdownx.tilde'],
            output_format='html5'
        )
        full_html = template.render(html_body=html_body)
        full_html = full_html.replace('Ã—', 'x') # Multiplication symbol gets messed up

    # Saving the final (full) HTML -- same name as MarkDown file with datetime attached
    if testing:
        html_file_name = md_file.stem + '_' + datetime.now().strftime('%Y-%m-%d_%H-%M') + '.html'
    else:
        html_file_name = str(md_file.stem).lower() + '.html'
        
    with open(html_file_name, 'w', encoding='utf-8') as f:
        f.write(full_html)
        print(f'Created {html_file_name}!\n')

    # Opening files!
    if open_browser:
        chrome_path="C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"
        webbrowser.register('chrome', None,webbrowser.BackgroundBrowser(chrome_path))
        webbrowser.get('chrome').open_new_tab(str(Path.cwd().joinpath(html_file_name)))

print('\nDone :-)\n')