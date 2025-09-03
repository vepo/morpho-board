import { Component, Input, Output, EventEmitter, ViewChild, ElementRef, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-rich-text-editor',
  templateUrl: './rich-text-editor.component.html',
  styleUrls: ['./rich-text-editor.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class RichTextEditorComponent implements AfterViewInit, OnChanges {
  @Input() placeholder: string = 'Digite seu texto...';
  @Input() value: string = '';
  @Input() disabled: boolean = false;
  @Output() valueChange = new EventEmitter<string>();

  @ViewChild('editor') editorRef!: ElementRef<HTMLDivElement>;

  isBold = false;
  isItalic = false;
  isUnderline = false;
  isList = false;

  ngAfterViewInit() {
    this.setupEditor();
  }

  ngOnChanges(changes: SimpleChanges): void {
    // indetify external change on value
    if (this.editorRef && changes && changes['value'] && !changes['value'].previousValue && changes['value'].currentValue && changes['value'].firstChange) {
      this.editorRef.nativeElement.innerHTML = changes['value'].currentValue;
    } else if (this.editorRef && changes['value'] && changes['value'].currentValue == '' && changes['value'].previousValue != '') {
      this.editorRef.nativeElement.innerHTML = '';
    }
  }

  setupEditor() {
    if (this.editorRef) {
      this.editorRef.nativeElement.innerHTML = this.value;
      this.editorRef.nativeElement.addEventListener('input', () => {
        this.value = this.editorRef.nativeElement.innerHTML;
        this.valueChange.emit(this.value);
      });
    }
  }

  formatText(command: string, value: string = '') {
    document.execCommand(command, false, value);
    this.editorRef.nativeElement.focus();
    this.updateToolbarState();
    this.emitChange();
  }

  updateToolbarState() {
    this.isBold = document.queryCommandState('bold');
    this.isItalic = document.queryCommandState('italic');
    this.isUnderline = document.queryCommandState('underline');
  }

  emitChange() {
    this.value = this.editorRef.nativeElement.innerHTML;
    this.valueChange.emit(this.value);
  }

  insertLink() {
    const url = prompt('Digite a URL:');
    if (url) {
      this.formatText('createLink', url);
    }
  }

  insertList() {
    this.formatText('insertUnorderedList');
    this.isList = !this.isList;
  }

  clearFormatting() {
    this.formatText('removeFormat');
    this.updateToolbarState();
  }

  getPlainText(): string {
    if (this.editorRef && this.editorRef.nativeElement) {
      return this.editorRef.nativeElement.innerText || '';
    } else {
      return '';
    }
  }

  getHtml(): string {
    if (this.editorRef && this.editorRef.nativeElement) {
      return this.editorRef.nativeElement.innerHTML || '';
    } else {
      return '';
    }
  }
} 