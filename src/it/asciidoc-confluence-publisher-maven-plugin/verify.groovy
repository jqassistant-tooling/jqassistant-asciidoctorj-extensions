File assetsDir = new File(basedir, 'target/asciidoc-confluence-publisher/assets')
File subDir = assetsDir.listFiles()?.find { it.isDirectory() }

def expectedFiles = ['index.html', 'it_CSVReport.csv', 'it_UndefinedComponentDependency.csv']

expectedFiles.each { filename ->
    File targetFile = new File(subDir, filename)
    assert targetFile.exists() : "Expected file missing: ${targetFile.absolutePath}"
}

File indexHtmlFile = new File(subDir, 'index.html')
assert indexHtmlFile.text.contains("it:CSVReport")