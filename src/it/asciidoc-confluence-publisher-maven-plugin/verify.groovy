def indexHtmlFile = new File(basedir, 'target/asciidoc-confluence-publisher/assets/975367e2001eda39fe8c811ff454339ca90f60c00d3882ea586142233d3020fc/index.html')
assert indexHtmlFile.exists()
assert indexHtmlFile.text.contains("it:CSVReport")
