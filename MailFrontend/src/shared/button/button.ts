import { Component, Input, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  // Use it on <button> or <a>
  selector: 'button[app-button], a[app-button]',
  standalone: true,
  imports: [CommonModule],
  styleUrls: ['./button.css'],
  encapsulation: ViewEncapsulation.None,
  template: `<ng-content></ng-content>`,
  host: {
    '[class]': 'computedClass',
    '[attr.disabled]': 'disabled ? true : null',
    '[attr.aria-disabled]': 'disabled'
  }
})
export class ButtonComponent {
  @Input() variant: 'default' | 'destructive' | 'outline' | 'ghost' | 'link' = 'default';
  @Input() size: 'default' | 'sm' | 'lg' | 'icon' = 'default';
  @Input() class = ''; // Allow extra classes from outside
  @Input() disabled = false;

  get computedClass(): string {
    // Always have the base '.btn' class
    const classes = ['btn'];

    // Add the Variant class (e.g., .btn-destructive)
    classes.push(`btn-${this.variant}`);

    // Add the Size class
    if (this.size === 'default') {
      classes.push('btn-default-size');
    } else {
      classes.push(`btn-${this.size}`);
    }

    // Handle disabled state manually for links
    if (this.disabled) {
      classes.push('disabled');
    }

    // Add any custom classes passed in
    if (this.class) {
      classes.push(this.class);
    }

    return classes.join(' ');
  }
}
