function focusOnFirstInput() {

	var forms = document.forms;
	var len = forms.length;

	for (var i = 0; i < len; i++) {
		var form = forms[i];
		for (var j = 0; j < form.length; j++) {
			var input = form[j];
			if (input.type != "hidden"
			&& input.type != "button"
			&& input.type != "submit") {

				if (!input.disabled) {
					input.focus();
					return;
				}
			}
		}
	}
}