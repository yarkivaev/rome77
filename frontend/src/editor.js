require.config({ paths: { 'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.34.1/min/vs' }});

require(['vs/editor/editor.main'], function() {

    // 1. Register Custom Language
    monaco.languages.register({ id: 'rome77' });

    monaco.languages.setLanguageConfiguration('rome77', {
        comments: {
            lineComment: '//',
            blockComment: ['/*', '*/']
        },
        brackets: [['(', ')']],
        autoClosingPairs: [{ open: '(', close: ')' }],
        surroundingPairs: [{ open: '(', close: ')' }],
        folding: {
            markers: {
                start: new RegExp("^\\s*//\\s*#?region\\b"),
                end: new RegExp("^\\s*//\\s*#?endregion\\b")
            }
        }
    });

    // 2. Define Syntax Highlighting (Tokenizer)
    monaco.languages.setMonarchTokensProvider('rome77', {
        tokenizer: {
            root: [
                // Keywords
                [/Munus/, 'keyword'],
                [/As/, 'keyword'],
                [/Anagnosi/, 'keyword'],
                [/Grafo/, 'keyword'],
                [/Sinon/, 'keyword'],

                // Roman Numerals & Zero
                [/[IVXLCDM]+/, 'number'],
                [/N\b/, 'number'],

                // Identifiers (lowercase)
                [/[a-z][a-z0-9]*/, 'identifier'],

                // Operators
                [/[+\-*/=]/, 'operator'],

                // Comments
                [/\/\/.*$/, 'comment'],
                [/\/\*/, 'comment', '@comment'],

                // Strings (optional, but good for completeness)
                [/"/, 'string', '@string'],

                // Whitespace
                [/\s+/, 'white']
            ],
            comment: [
                [/[^*/]+/, 'comment'],
                [/\/\*/, 'comment', '@push'],
                ["\\*/", 'comment', '@pop'],
                [/./, 'comment']
            ],
            string: [
                [/[^\\"]+/, 'string'],
                [/\\./, 'string.escape'],
                [/"/, 'string', '@pop']
            ]
        }
    });

    // 3. Initialize Editor
    const editor = monaco.editor.create(document.getElementById('editor-container'), {
        value: [
            '// Rome77 Example: Fibonacci',
            'Munus fib n = Sinon n I ((fib n - I) + (fib n - II))',
            '',
            'As n = Anagnosi',
            'Grafo fib n',
            '',
            'As x = y + 5',
            ''
        ].join('\n'),
        language: 'rome77',
        theme: 'vs-dark',
        automaticLayout: true,
        fontSize: 14,
        minimap: { enabled: true },
        // Ensure these are enabled (though config above usually handles it)
        autoClosingBrackets: 'always',
        autoClosingQuotes: 'always',
        formatOnPaste: true,
        suggestOnTriggerCharacters: true
    });

    window.editorInstance = editor;
    updateStatus("Ready");
});

async function runCode() {
    const code = window.editorInstance.getValue();
    const consoleDiv = document.getElementById('console');
    const loading = document.getElementById('loading');
    const btn = document.getElementById('runBtn');
    const statusBar = document.getElementById('statusBar');

    consoleDiv.innerHTML = '';
    // Clear existing markers
    monaco.editor.setModelMarkers(window.editorInstance.getModel(), 'owner', []);

    loading.style.display = 'flex';
    btn.disabled = true;
    updateStatus("Compiling...");

    try {
        const response = await fetch('http://localhost:8080/compile', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ source: code })
        });

        if (!response.ok) throw new Error(`Server returned ${response.status}`);

        const result = await response.json();

        if (result.output) {
            printLog("=== Execution Output ===", 'log-info');
            printLog(result.output, 'log-success');
        }

        if (result.error) {
            printLog(`Critical Error: ${result.error}`, 'log-error');
        }

        if (result.diagnostics && result.diagnostics.length > 0) {
            printLog(`=== Found ${result.diagnostics.length} Issue(s) ===`, 'log-warning');

            const markers = result.diagnostics.map(d => {
                const type = d.severity === 'error' ? 'log-error' : 'log-warning';
                printLog(`[${d.severity.toUpperCase()}] Line ${d.startLine}: ${d.message}`, type);

                return {
                    severity: d.severity === 'error' ? monaco.MarkerSeverity.Error : monaco.MarkerSeverity.Warning,
                    startLineNumber: d.startLine,
                    startColumn: d.startColumn || 1,
                    endLineNumber: d.endLine || d.startLine,
                    endColumn: d.endColumn || 1,
                    message: d.message,
                    source: 'Rome77 Compiler'
                };
            });

            monaco.editor.setModelMarkers(window.editorInstance.getModel(), 'owner', markers);
            updateStatus(`Issues: ${markers.length}`);
        } else if (!result.error && !result.output) {
            printLog("Compilation successful.", 'log-success');
            updateStatus("Success");
        }

    } catch (err) {
        printLog(`Connection Failed: ${err.message}`, 'log-error');
        updateStatus("Connection Error");
    } finally {
        loading.style.display = 'none';
        btn.disabled = false;
    }
}

function printLog(text, className) {
    const consoleDiv = document.getElementById('console');
    const line = document.createElement('div');
    line.textContent = text;
    line.className = `log-entry ${className || ''}`;
    consoleDiv.appendChild(line);
    consoleDiv.scrollTop = consoleDiv.scrollHeight;
}

function updateStatus(msg) {
    document.getElementById('statusBar').textContent = msg;
}
