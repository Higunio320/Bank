import {AbstractControl, FormGroup, ValidationErrors, ValidatorFn} from "@angular/forms";

export class CustomValidators {

  static alphanumericValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if(!value || /^[a-zA-Z0-9 ]*$/.test(value)) {
        return null;
    } else {
        return {'alphanumeric': true}
      }
    }
  }

  static passwordEntropyValidator(minEntropy: number) : ValidatorFn {
    return (control: AbstractControl) : ValidationErrors | null => {
      const password = control.value;
      if(!password) {
        return null;
      }

      const entropy = this.calculateEntropy(password);

      if(entropy < minEntropy) {
        return {'minentropy': true}
      }

      return null;
    }
  }

  static passwordRegexValidator(regex: RegExp) : ValidatorFn {
    return (control: AbstractControl) : ValidationErrors | null => {
      const password = control.value;

      if(!password || regex.test(password)) {
        return null;
      } else {
        return {'regex': true}
      }
    }
  }

  static matchValidator(field1: string, field2: string) : ValidatorFn {
    return (group: AbstractControl) : ValidationErrors | null => {
      const formGroup = group as FormGroup;
      const value1 = formGroup.controls[field1].value;
      const value2 = formGroup.controls[field2].value;

      if (value1 !== value2) {
        return {'mismatch': true};
      }

      return null;
    }
  }

  static doubleNumberValidator() : ValidatorFn {
    return (control: AbstractControl) : ValidationErrors | null => {
      const value = control.value;
      if(!value || /^[1-9]\d*\.\d{2}$/.test(value)) {
        return null;
      }
      return {'doublenumber': true};
    }
  }

  static onlyNumbersValidator() : ValidatorFn {
    return (control: AbstractControl) : ValidationErrors | null => {
      const value = control.value;

      if(!value || /^\d+$/.test(value)) {
        return null;
      }

      return {'onlynumbers': true};
    }
  }

  static fixedLengthValidator(length: number) : ValidatorFn {
    return (control: AbstractControl) : ValidationErrors | null => {
      const value = control.value;

      if(!value || value.length === length) {
        return null;
      }

      return {'fixedlength': true};
    }
  }

  private static calculateEntropy(password: string) {
    const charMap = new Map<string, number>();

    for(let char of password) {
      if(charMap.has(char)) {
        charMap.set(char, charMap.get(char)! + 1);
      } else {
        charMap.set(char, 1);
      }
    }

    let entropy = 0.0;

    for(let num of charMap.values()) {
      let probability = num / password.length;
      entropy -= probability * Math.log2(probability)
    }

    return entropy;
  }
}
