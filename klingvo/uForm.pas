unit uForm;

interface

uses
  Classes, Controls, Forms, StdCtrls, uKL;

type
  TfrmMain = class(TForm)
    edtMain: TEdit;
    btnTranslate: TButton;
    procedure btnTranslateClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmMain: TfrmMain;

implementation
{$R *.dfm}

  procedure TfrmMain.btnTranslateClick(Sender: TObject);
  begin
    edtMain.Text := GetKLS(edtMain.Text);
  end;

end.
