var MonacoDesign = {
	initConfig : () =>{
		require.config({
            paths: { 'vs': './js/monaco/vs', 'editor': '.' },
            'vs/nls': { availableLanguages: { '*': 'zh-cn' } }
        });
	},
	json:{
		init : (selector) => {
			return new Promise((resolve, reject) => {
				require(['vs/editor/editor.main'], function () {
					try{
			            const language = 'json';
			            monaco.languages.register({ id: language });
			            // 语言交互特性配置
			            monaco.languages.setLanguageConfiguration(language, {
					        wordPattern: /(-?\d*\.\d\w*)|([^`~!#%^&*()\-=+[{\]}\\|;:'",.<>/?\s]+)/g,
					        brackets: [['{', '}'], ['[', ']'], ['(', ')']],
					        onEnterRules: [
			                    // 回车时自动补全 Javadoc 格式
			                    {
			                        beforeText: /^\s*\/\*\*(?!\/)([^\*]|\*(?!\/))*$/,
			                        afterText: /^\s*\*\/$/,
			                        action: { indentAction: monaco.languages.IndentAction.IndentOutdent, appendText: ' * ' }
			                    },
			                    {
			                        beforeText: /^\s*\/\*\*(?!\/)([^\*]|\*(?!\/))*$/,
			                        action: { indentAction: monaco.languages.IndentAction.None, appendText: ' * ' }
			                    }
					        ],
					        comments: { lineComment: '//', blockComment: ['/*', '*/'] },
					        operators: ['<=', '>=', '==', '!=', '+', '-', '*', '/', '%', '&', '|', '!', '&&', '||', '?', ':', '++', '--', '+=', '-=', '*=', '/='],
					        autoClosingPairs: [
					            { open: '{', close: '}' }, { open: '[', close: ']' }, { open: '(', close: ')' },
					            { open: '"""', close: '"""', notIn: ['string.multi'] },
					            { open: '"', close: '"', notIn: ['string'] },
					            { open: '\'', close: '\'', notIn: ['string'] },
					            { open: '/**', close: ' */', notIn: ['string'] }
					        ],
					    });
			
			            const jsonEditor = monaco.editor.create(document.getElementById(selector), {
			                value: '',
			                language: 'json',
			                theme: 'vs-dark',
			                automaticLayout: true,
			                minimap: { enabled: false },
			                scrollbar: { vertical: 'visible', horizontal: 'visible', useShadows: false }
			            });
		                resolve(jsonEditor); 
		            } catch (e) {
		                reject(e);
		            }
		        });
		    });
		}
	},
	html:{
		init : (selector) => {
			return new Promise((resolve, reject) => {
				require(['vs/editor/editor.main'], () => {
					try{
						const sfcEditor = monaco.editor.create(document.getElementById(selector), {
		                    value: '',
		                    language: 'html',
		                    theme: 'vs-dark',
		                    automaticLayout: true,
		                    minimap: { enabled: false },
		                    scrollbar: { vertical: 'visible', horizontal: 'visible', useShadows: false }
		                });
		                 resolve(sfcEditor); 
					} catch (e) {
		                reject(e);
		            }
	            }); 
	       });
		}
	}
	
	
}