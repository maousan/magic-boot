define([
	'./high-light.js'], 
function(highLight) {
    
    // 解构导入
    const { HighLightOptions } = highLight;



let scriptClass = {}
let extensions = {}
let importClass = []
let functions = []
let autoImportModule;
let autoImportClass;

const getWrapperClass = (target) => {
	if (target === 'int' || target === 'java.lang.Integer') {
		return 'java.lang.Integer';
	}
	if (target === 'string' || target === 'java.lang.String') {
		return 'java.lang.String';
	}
	if (target === 'double' || target === 'java.lang.Double') {
		return 'java.lang.Double';
	}
	if (target === 'float' || target === 'java.lang.Float') {
		return 'java.lang.Float';
	}
	if (target === 'byte' || target === 'java.lang.Byte') {
		return 'java.lang.Byte';
	}
	if (target === 'short' || target === 'java.lang.Short') {
		return 'java.lang.Short';
	}
	if (target === 'long' || target === 'java.lang.Long') {
		return 'java.lang.Long';
	}
	if (target.indexOf('[]') > -1) {
		return '[Ljava.lang.Object;';
	}
	return target || 'java.lang.Object';
}
const getSimpleClass = (target) => {
	let index = target.lastIndexOf('.')
	if (index > -1) {
		return target.substring(index + 1);
	}
	return target;
}
const matchTypes = (parameters, args, extension) => {
	if (parameters.length > 0 && parameters[parameters.length - 1].varArgs) {
		return extension ? parameters.length - 1 <= args.length : parameters.length <= args.length;
	} else {
		return extension ? parameters.length - 1 === args.length : parameters.length === args.length;
	}
}

const processNode = item => {
	return {
		...item.node,
		folder: item.node.parentId !== undefined,
		children: item.children&& item.children.length ? item.children.map(it => processNode(it)) : undefined
	}
}

const padding = (num, n) => Array(n > (num + '').length ? (n - ('' + num).length - 1) : 0).join(0) + num;

const findEnums = (clazz) => {
	let enums = []
	if (clazz) {
		enums = clazz.enums || [];
		if (clazz.superClass) {
			enums = enums.concat(findEnums(clazz.superClass));
		}
	}
	return enums;
}
const processMethod = (method, begin, sort) => {
	method.insertText = method.name;
	if (method.parameters.length > begin) {
		let params = [];
		let params2 = [];
		for (let j = begin; j < method.parameters.length; j++) {
			params.push('${' + (j + 1 - begin) + ':' + method.parameters[j].name + '}');
			if (method.parameters[j].varArgs) {
				params2.push(getSimpleClass(method.parameters[j].type).replace('[]', '') + " ... " + method.parameters[j].name);
			} else {
				params2.push(getSimpleClass(method.parameters[j].type) + " " + method.parameters[j].name);
			}
		}
		method.sortText = padding(sort, 10) + method.name;
		method.fullName = method.name + '(' + params2.join(', ') + ')';
		method.insertText += '(' + params.join(',') + ')';
		method.signature = method.name + params2.join(',');
	} else {
		method.sortText = padding(sort, 10) + method.name;
		method.insertText += '()';
		method.fullName = method.name + '()';
		method.signature = method.name;
	}
	return method;
}
let extensionAttribute = {};
const setExtensionAttribute = (clazz, value) => {
	extensionAttribute[clazz] = value;
}
const findAttributes = (clazz) => {
	let attributes = [];
	if (clazz) {
		attributes = clazz.attributes || [];
		if (clazz.superClass) {
			attributes = attributes.concat(findAttributes(clazz.superClass));
		}
		if (clazz.interfaces && clazz.interfaces.length > 0) {
			for (let i = 0, len = clazz.interfaces.length; i < len; i++) {
				attributes = attributes.concat(findAttributes(clazz.interfaces[i]));
			}
		}
		if (extensionAttribute[clazz.className]) {
			if (typeof extensionAttribute[clazz.className] === 'function') {
				attributes = attributes.concat(extensionAttribute[clazz.className]())
			} else {
				attributes = attributes.concat(extensionAttribute[clazz.className])
			}
			
		}
	}
	return attributes;
}
const findMethods = (clazz, sort) => {
	sort = sort || 0;
	let methods = [];
	let _findMethod = (target, begin, sort) => {
		if (target && target.methods) {
			for (let i = 0, len = target.methods.length; i < len; i++) {
				let method = target.methods[i];
				method = processMethod(method, begin, sort);
				method.extension = begin === 1;
				methods.push(method);
			}
		}
	}
	if (typeof clazz === 'string') {
		clazz = scriptClass[clazz];
	}
	if (clazz) {
		_findMethod(clazz, 0, sort);
		if (clazz.superClass) {
			methods = methods.concat(findMethods(clazz.superClass, sort + 1));
		}
		if (clazz.interfaces && clazz.interfaces.length > 0) {
			for (let i = 0, len = clazz.interfaces.length; i < len; i++) {
				methods = methods.concat(findMethods(clazz.interfaces[i], sort + 100));
			}
		}
		clazz = extensions[clazz.className];
		if (clazz) {
			_findMethod(clazz, 1, sort + 10000);
		}
	}
	return methods;
}

const getExtension = (clazz) => {
	return extensions[clazz]
}
const findClass = (className) => {
	if (!className) {
		throw new Error('className is required');
	}
	let value = scriptClass[className]
	if (!value) {
		let index = importClass.findIndex(it => it === className)
		value = importClass[index]
	}
	return value
}

const findFunction = () => {
	return functions.map(method => processMethod(method, 0, 1));
}

const initAutoImport = () => {
	if (!autoImportModule ) {
		let config = {};
		if (config.autoImportModuleList) {
			autoImportModule = {};
			config.autoImportModuleList.forEach(it => {
				autoImportModule[it] = it;
			})
		}
		let importPackages = ['java.util.', 'java.lang.'].concat((config.autoImportPackage || '').replace(/\\s/g, '').replace(/\*/g, '').split(','));
		autoImportClass = {};
		importClass.forEach(className => {
			importPackages.forEach(packageName => {
				if (className.indexOf(packageName) === 0 && className.indexOf(".", packageName.length) === -1) {
					autoImportClass[className.substring(className.lastIndexOf('.') + 1)] = className;
				}
			})
		})
	}
}
const getAutoImportModule = () => {
	initAutoImport();
	return autoImportModule || {}
}
const getAutoImportClass = () => {
	initAutoImport();
	return autoImportClass || {}
}
const getImportClass = () => importClass;
let onlineFunctionFinder;
const setupOnlineFunction = (loader) => {
	onlineFunctionFinder = loader;
}
const getOnlineFunction = (path) => {
	return onlineFunctionFinder && onlineFunctionFinder(path);
}
const getDefineModules = () => Object.keys(scriptClass).filter(it => scriptClass[it].module)
let apiFinder;
const setApiFinder = (finder) => {
	apiFinder = finder;
}
let functionFinder;
const setFunctionFinder = (finder) => {
	functionFinder = finder;
}
const getApiFinder = () => apiFinder
const getFunctionFinder = () => functionFinder
const exportValue = {
	findEnums,
	findAttributes,
	findMethods,
	findFunction,
	findClass,
	getWrapperClass,
	matchTypes,
	getAutoImportModule,
	getAutoImportClass,
	getExtension,
	getImportClass,
	getOnlineFunction,
	setupOnlineFunction,
	setExtensionAttribute,
	getSimpleClass,
	getDefineModules,
	setApiFinder,
	setFunctionFinder,
	getApiFinder,
	getFunctionFinder,


}
    // 返回提供者
    return exportValue;
});
