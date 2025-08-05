import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatBadgeModule } from '@angular/material/badge';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
  selector: 'app-function-card',
  standalone: true,
  imports: [
    MatIconModule,
    MatBadgeModule,
    MatTooltip
  ],
  templateUrl: './function-card.component.html',
  styleUrl: './function-card.component.scss'
})
export class FunctionCardComponent {
  @Input() title!: string;
  @Input() description = '';
  @Input() icon?: string;
  @Input() link?: string;
  @Input() badgeCount?: number;
  @Output() clickCard = new EventEmitter<void>();
}
