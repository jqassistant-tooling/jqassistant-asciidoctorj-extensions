File assetsDir = new File(basedir, 'target/asciidoc-confluence-publisher/assets')

File indexHtmlFile = assetsDir.listFiles()
        ?.collect { dir -> new File(dir, 'index.html') }
        ?.find { file -> file.exists() }

assert indexHtmlFile.exists()
assert indexHtmlFile.text.contains("it:CSVReport")
