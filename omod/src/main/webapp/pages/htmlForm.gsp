${ ui.includeFragment(fragmentProvider, fragmentName, [
        patient: patient,
        encounter: encounter,
        definitionUiResource: 'pihmalawi:htmlforms/' + formName + '.xml',
        returnUrl: returnUrl
]) }