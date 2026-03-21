require.config({ paths: { 'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.34.1/min/vs' }});

require(['vs/editor/editor.main'], function() {

    // 1. Register Custom Language "rome77"
    monaco.languages.register({ id: 'rome77' });

    // 2. Define Syntax Highlighting Rules
    monaco.languages.setMonarchTokensProvider('rome77', {
        tokenizer: {
            root: [
                // Keywords
                [/Munus/, 'keyword'],
                [/As/, 'keyword'],
                [/Anagnosi/, 'keyword'],
                [/Grafo/, 'keyword'],
                [/Sinon/, 'keyword'],

                // Roman Numerals & Zero (N)
                [/[IVXLCDM]+/, 'number'],
                [/N\b/, 'number'],

                // Identifiers (lowercase only per spec)
                [/[a-z][a-z0-9]*/, 'identifier'],

                // Operators
                [/[+\-*/=]/, 'operator'],

                // Comments (assuming standard // or # if not specified, adding // just in case)
                [/\/\/.*$/, 'comment'],

                // Whitespace is handled automatically
            ]
        }
    });

    // 3. Define Theme Colors (Optional, defaults are fine but this makes keywords pop)
    monaco.editor.defineTheme('romeTheme', {
        base: 'vs-dark',
        inherit: true,
        rules: [
            { token: 'keyword', foreground: 'c586c0', fontStyle: 'bold' },
            { token: 'number', foreground: 'b5cea8' },
            { token: 'identifier', foreground: '9cdcfe' },
            { token: 'operator', foreground: 'd4d4d4' }
        ],
        colors: {}
    });

    // 4. Initialize Editor
    const editor = monaco.editor.create(document.getElementById('editor-container'), {
        value: [
            'Munus fib n = Sinon n I ((fib n - I) + (fib n - II))',
            '',
            'As n = Anagnosi',
            'Grafo fib n',
            ''
        ].join('\n'),
        language: 'rome77',
        theme: 'romeTheme',
        automaticLayout: true,
        fontSize: 14,
        minimap: { enabled: false }
    });

    // Expose editor globally for the run function
    window.editorInstance = editor;
});

// 5. Logic to Run Code
async function runCode() {
    const code = window.editorInstance.getValue();
    const consoleDiv = document.getElementById('console');
    const loading = document.getElementById('loading');
    const btn = document.getElementById('runBtn');

    // Clear previous output
    consoleDiv.innerHTML = '';
    loading.style.display = 'flex';
    btn.disabled = true;

    try {
        // Send to Backend
        const response = await fetch('http://localhost:8080/compile', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ source: code })
        });

        const result = await response.json();

        // Handle Output
        if (result.error) {
            printLog(`Error: ${result.error}`, 'log-error');
        } else if (result.output) {
            printLog(result.output, 'log-success');
        } else {
            printLog("Compilation successful (no output returned).", 'log-info');
        }

    } catch (err) {
        printLog(`Connection Failed: ${err.message}. Is the server running on localhost:8080?`, 'log-error');
    } finally {
        loading.style.display = 'none';
        btn.disabled = false;
    }
}

function printLog(text, className) {
    const consoleDiv = document.getElementById('console');
    const line = document.createElement('div');
    line.textContent = text;
    if (className) line.classList.add(className);
    consoleDiv.appendChild(line);
    consoleDiv.scrollTop = consoleDiv.scrollHeight;
}
