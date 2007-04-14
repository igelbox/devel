object frmMain: TfrmMain
  Left = 205
  Top = 106
  BorderStyle = bsDialog
  Caption = #1050#1091#1085#1080'Lingvo v0.1a'
  ClientHeight = 37
  ClientWidth = 312
  Color = clAppWorkSpace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  PixelsPerInch = 96
  TextHeight = 13
  object edtMain: TEdit
    Left = 7
    Top = 7
    Width = 218
    Height = 24
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clWindowText
    Font.Height = -13
    Font.Name = 'MS Sans Serif'
    Font.Style = [fsBold]
    ParentFont = False
    TabOrder = 0
  end
  object btnTranslate: TButton
    Left = 232
    Top = 7
    Width = 75
    Height = 25
    Caption = #1055#1077#1088#1077#1077#1087#1089#1090#1080
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clWindowText
    Font.Height = -11
    Font.Name = 'MS Sans Serif'
    Font.Style = [fsBold]
    ParentFont = False
    TabOrder = 1
    OnClick = btnTranslateClick
  end
end
