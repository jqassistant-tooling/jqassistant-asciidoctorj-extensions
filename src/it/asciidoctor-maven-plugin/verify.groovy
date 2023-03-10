def indexHtmlFile = new File(basedir, 'target/generated-docs/index.html')
assert indexHtmlFile.exists()
assert indexHtmlFile.text.contains("<h2 id=\"_summary\">Summary</h2>")
assert indexHtmlFile.text.contains("<h3 id=\"_itcsvreport\">it:CSVReport</h3>")