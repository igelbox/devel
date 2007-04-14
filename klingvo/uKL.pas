unit uKL;
interface
uses SysUtils;

  function IsGlasnaya(c: Char): Boolean;
  function IsSSound(c: Char): Boolean;
  function IsSoglasnaya(c: Char): Boolean;
  function GetAfterS(s: string): string;
  function GetKLS(s: string): string;
implementation
  function IsForbidden(c: Char): Boolean;        begin Result := Pos(c, 'abcdefghijklmnopqrstuvwxyz')<>0; end;
  function IsGlasnaya(c: Char): Boolean;        begin Result := Pos(c, 'аеёиоуыэюя')<>0; end;
  function IsSoglasnaya(c: Char): Boolean;      begin Result := Pos(c, 'бвгджзклмнпрстфхцчшщ')<>0; end;
  function IsSSound(c: Char): Boolean;          begin Result := Pos(c, 'йь')<>0; end;
  function GetAfterS(s: string): string;
  var Ch, XC, SPos: Integer;
    function IsGSS(c: Char): Boolean;   begin Result := IsGlasnaya(c) or IsSSound(c); end;
  begin
    SPos := Length(s); XC := 0;
    for Ch := Length(s) downto 2 do
    begin
      if IsGSS(s[Ch]) and not IsGSS(s[Ch-1]) then
      begin
        Inc(XC);
        if XC = 4 then Break else SPos := Ch;
      end;
    end;
    Result := Copy(s, SPos, Length(s)-SPos+1);
  end;
  function GetKLS(s: string): string;
  var Ch: Integer; afs: string;
  begin
    s := LowerCase(s);
    for Ch := 1 to Length(s) do
      if IsForbidden(s[Ch]) then
      begin
        Result := 'ЭРОр! форбидэн чарактерс';
        Exit;
      end;
    Result := '';
    afs := GetAfterS(s);
    if Length(afs)=0 then Exit;
    case afs[1] of
      'а' : afs[1] := 'я';
      'о' : afs[1] := 'ё';
      'у' : afs[1] := 'ю';
      'ы' : afs[1] := 'и';
      'э' : afs[1] := 'е';
    end;
    if Length(afs)>=2 then
      if afs[1] = afs[2] then Delete(afs, 1, 1);
    Result := 'ху'+afs;
  end;

end.
