MathJax.Hub.Config({
    "jax": ["input/TeX","output/SVG"],
    "showProcessingMessages": false,
    "tex2jax": {"inlineMath": [["$","$"], ["\\(","\\)"]]},
    "ignoreClass": "[a-zA-Z1-9]*",
    "processClass": "question",
    "messageStyle": "none",
    "SVG": {"linebreaks": { "automatic": true }},
    "TeX": {
        "Macros": {
            "longdiv": ["{\\overline{\\smash{)}#1}}", 1]
        }
    },
    "HTML-CSS": {
        "linebreaks": { "automatic": true }
    },
    "CommonHTML": { "linebreaks": { "automatic": true } }
});