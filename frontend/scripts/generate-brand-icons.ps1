param(
  [string]$OutputDir = (Join-Path $PSScriptRoot '..\static\brand-icons'),
  [int]$Size = 96
)

Add-Type -AssemblyName System.Drawing

function New-Color($hex) {
  return [System.Drawing.ColorTranslator]::FromHtml($hex)
}

function Add-RoundedRect {
  param(
    [System.Drawing.Drawing2D.GraphicsPath]$Path,
    [float]$X,
    [float]$Y,
    [float]$Width,
    [float]$Height,
    [float]$Radius
  )

  $diameter = $Radius * 2
  $Path.AddArc($X, $Y, $diameter, $diameter, 180, 90)
  $Path.AddArc($X + $Width - $diameter, $Y, $diameter, $diameter, 270, 90)
  $Path.AddArc($X + $Width - $diameter, $Y + $Height - $diameter, $diameter, $diameter, 0, 90)
  $Path.AddArc($X, $Y + $Height - $diameter, $diameter, $diameter, 90, 90)
  $Path.CloseFigure()
}

function New-Pen {
  param(
    [System.Drawing.Color]$Color,
    [float]$Width
  )

  $pen = New-Object System.Drawing.Pen($Color, $Width)
  $pen.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
  $pen.EndCap = [System.Drawing.Drawing2D.LineCap]::Round
  $pen.LineJoin = [System.Drawing.Drawing2D.LineJoin]::Round
  return $pen
}

function New-GraphicsContext {
  param([int]$CanvasSize)

  $bitmap = New-Object System.Drawing.Bitmap($CanvasSize, $CanvasSize, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
  $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
  $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
  $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
  $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
  $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality

  return @{
    Bitmap = $bitmap
    Graphics = $graphics
  }
}

function Get-Palette {
  param(
    [string]$Name,
    [string]$Tone
  )

  $defaultPalette = @{
    Fill = New-Color '#F2EBE3'
    Border = New-Color '#D8C9BC'
    Stroke = New-Color '#8B7B6F'
    Accent = New-Color '#A9998D'
  }

  $accentPalettes = @{
    home = @{
      Fill = New-Color '#F7E7D3'
      Border = New-Color '#EDD0AD'
      Stroke = New-Color '#8C5C3A'
      Accent = New-Color '#D79A60'
    }
    spark = @{
      Fill = New-Color '#F9E6CD'
      Border = New-Color '#EFCDA3'
      Stroke = New-Color '#8C5C3A'
      Accent = New-Color '#E0A451'
    }
    food = @{
      Fill = New-Color '#FBDCCA'
      Border = New-Color '#F0BA99'
      Stroke = New-Color '#9A4A2A'
      Accent = New-Color '#DE6E2E'
    }
    filter = @{
      Fill = New-Color '#FBDCCA'
      Border = New-Color '#F0BA99'
      Stroke = New-Color '#9A4A2A'
      Accent = New-Color '#DE6E2E'
    }
    edit = @{
      Fill = New-Color '#FBDCCA'
      Border = New-Color '#F0BA99'
      Stroke = New-Color '#9A4A2A'
      Accent = New-Color '#DE6E2E'
    }
    list = @{
      Fill = New-Color '#FBDCCA'
      Border = New-Color '#F0BA99'
      Stroke = New-Color '#9A4A2A'
      Accent = New-Color '#DE6E2E'
    }
    history = @{
      Fill = New-Color '#F7E7D3'
      Border = New-Color '#EDD0AD'
      Stroke = New-Color '#8C5C3A'
      Accent = New-Color '#D79A60'
    }
    checkin = @{
      Fill = New-Color '#DEE7FF'
      Border = New-Color '#BFCFFF'
      Stroke = New-Color '#4C65CC'
      Accent = New-Color '#6E8CF7'
    }
    stats = @{
      Fill = New-Color '#DEE7FF'
      Border = New-Color '#BFCFFF'
      Stroke = New-Color '#4C65CC'
      Accent = New-Color '#6E8CF7'
    }
    board = @{
      Fill = New-Color '#DEE7FF'
      Border = New-Color '#BFCFFF'
      Stroke = New-Color '#4C65CC'
      Accent = New-Color '#6E8CF7'
    }
    calendar = @{
      Fill = New-Color '#DEE7FF'
      Border = New-Color '#BFCFFF'
      Stroke = New-Color '#4C65CC'
      Accent = New-Color '#6E8CF7'
    }
    mine = @{
      Fill = New-Color '#ECDDCE'
      Border = New-Color '#D8C0AE'
      Stroke = New-Color '#6B5146'
      Accent = New-Color '#9F7D68'
    }
    key = @{
      Fill = New-Color '#ECDDCE'
      Border = New-Color '#D8C0AE'
      Stroke = New-Color '#6B5146'
      Accent = New-Color '#9F7D68'
    }
    wake = @{
      Fill = New-Color '#ECDDCE'
      Border = New-Color '#D8C0AE'
      Stroke = New-Color '#6B5146'
      Accent = New-Color '#9F7D68'
    }
  }

  if ($Tone -eq 'default') {
    return $defaultPalette
  }

  if ($accentPalettes.ContainsKey($Name)) {
    return $accentPalettes[$Name]
  }

  return $accentPalettes.home
}

function Draw-Badge {
  param(
    [System.Drawing.Graphics]$Graphics,
    [hashtable]$Palette,
    [int]$CanvasSize
  )

  $path = New-Object System.Drawing.Drawing2D.GraphicsPath
  Add-RoundedRect -Path $path -X ($CanvasSize * 0.10) -Y ($CanvasSize * 0.10) -Width ($CanvasSize * 0.80) -Height ($CanvasSize * 0.80) -Radius ($CanvasSize * 0.22)
  $fillBrush = New-Object System.Drawing.SolidBrush($Palette.Fill)
  $borderPen = New-Pen -Color $Palette.Border -Width ($CanvasSize * 0.03)

  $Graphics.FillPath($fillBrush, $path)
  $Graphics.DrawPath($borderPen, $path)

  $fillBrush.Dispose()
  $borderPen.Dispose()
  $path.Dispose()
}

function Draw-HomeIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.07)
  $accentBrush = New-Object System.Drawing.SolidBrush($Palette.Accent)

  $roof = @(
    [System.Drawing.PointF]::new($CanvasSize * 0.27, $CanvasSize * 0.49),
    [System.Drawing.PointF]::new($CanvasSize * 0.50, $CanvasSize * 0.29),
    [System.Drawing.PointF]::new($CanvasSize * 0.73, $CanvasSize * 0.49)
  )
  $Graphics.DrawLines($pen, $roof)
  $Graphics.DrawLines($pen, @(
    [System.Drawing.PointF]::new($CanvasSize * 0.33, $CanvasSize * 0.47),
    [System.Drawing.PointF]::new($CanvasSize * 0.33, $CanvasSize * 0.69),
    [System.Drawing.PointF]::new($CanvasSize * 0.67, $CanvasSize * 0.69),
    [System.Drawing.PointF]::new($CanvasSize * 0.67, $CanvasSize * 0.47)
  ))
  $Graphics.DrawLine($pen, $CanvasSize * 0.49, $CanvasSize * 0.69, $CanvasSize * 0.49, $CanvasSize * 0.54)
  $Graphics.FillEllipse($accentBrush, $CanvasSize * 0.60, $CanvasSize * 0.34, $CanvasSize * 0.09, $CanvasSize * 0.09)

  $pen.Dispose()
  $accentBrush.Dispose()
}

function Draw-FoodIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.065)
  $accentPen = New-Pen -Color $Palette.Accent -Width ($CanvasSize * 0.045)
  $accentBrush = New-Object System.Drawing.SolidBrush($Palette.Accent)

  $Graphics.DrawArc($pen, $CanvasSize * 0.24, $CanvasSize * 0.44, $CanvasSize * 0.52, $CanvasSize * 0.23, 0, 180)
  $Graphics.DrawLine($pen, $CanvasSize * 0.29, $CanvasSize * 0.57, $CanvasSize * 0.71, $CanvasSize * 0.57)
  $Graphics.DrawLine($accentPen, $CanvasSize * 0.60, $CanvasSize * 0.29, $CanvasSize * 0.69, $CanvasSize * 0.45)
  $Graphics.DrawLine($accentPen, $CanvasSize * 0.66, $CanvasSize * 0.27, $CanvasSize * 0.75, $CanvasSize * 0.43)
  $Graphics.FillEllipse($accentBrush, $CanvasSize * 0.34, $CanvasSize * 0.45, $CanvasSize * 0.08, $CanvasSize * 0.08)
  $Graphics.FillEllipse($accentBrush, $CanvasSize * 0.45, $CanvasSize * 0.39, $CanvasSize * 0.08, $CanvasSize * 0.08)

  $pen.Dispose()
  $accentPen.Dispose()
  $accentBrush.Dispose()
}

function Draw-CheckinIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.065)
  $accentPen = New-Pen -Color $Palette.Accent -Width ($CanvasSize * 0.05)
  $Graphics.DrawEllipse($pen, $CanvasSize * 0.27, $CanvasSize * 0.27, $CanvasSize * 0.46, $CanvasSize * 0.46)
  $Graphics.DrawLine($pen, $CanvasSize * 0.50, $CanvasSize * 0.36, $CanvasSize * 0.50, $CanvasSize * 0.50)
  $Graphics.DrawLine($pen, $CanvasSize * 0.50, $CanvasSize * 0.50, $CanvasSize * 0.61, $CanvasSize * 0.57)
  $Graphics.DrawLines($accentPen, @(
    [System.Drawing.PointF]::new($CanvasSize * 0.36, $CanvasSize * 0.52),
    [System.Drawing.PointF]::new($CanvasSize * 0.46, $CanvasSize * 0.61),
    [System.Drawing.PointF]::new($CanvasSize * 0.64, $CanvasSize * 0.41)
  ))

  $pen.Dispose()
  $accentPen.Dispose()
}

function Draw-MineIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.065)
  $accentBrush = New-Object System.Drawing.SolidBrush($Palette.Accent)
  $Graphics.DrawEllipse($pen, $CanvasSize * 0.37, $CanvasSize * 0.28, $CanvasSize * 0.26, $CanvasSize * 0.24)
  $Graphics.DrawArc($pen, $CanvasSize * 0.28, $CanvasSize * 0.49, $CanvasSize * 0.44, $CanvasSize * 0.24, 200, 140)
  $Graphics.FillEllipse($accentBrush, $CanvasSize * 0.58, $CanvasSize * 0.57, $CanvasSize * 0.10, $CanvasSize * 0.10)

  $pen.Dispose()
  $accentBrush.Dispose()
}

function Draw-SparkIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.06)
  $accentBrush = New-Object System.Drawing.SolidBrush($Palette.Accent)
  $Graphics.DrawLine($pen, $CanvasSize * 0.50, $CanvasSize * 0.27, $CanvasSize * 0.50, $CanvasSize * 0.68)
  $Graphics.DrawLine($pen, $CanvasSize * 0.29, $CanvasSize * 0.48, $CanvasSize * 0.70, $CanvasSize * 0.48)
  $Graphics.DrawLine($pen, $CanvasSize * 0.36, $CanvasSize * 0.34, $CanvasSize * 0.64, $CanvasSize * 0.62)
  $Graphics.DrawLine($pen, $CanvasSize * 0.64, $CanvasSize * 0.34, $CanvasSize * 0.36, $CanvasSize * 0.62)
  $Graphics.FillEllipse($accentBrush, $CanvasSize * 0.67, $CanvasSize * 0.26, $CanvasSize * 0.09, $CanvasSize * 0.09)

  $pen.Dispose()
  $accentBrush.Dispose()
}

function Draw-FilterIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.065)
  $accentBrush = New-Object System.Drawing.SolidBrush($Palette.Accent)
  $Graphics.DrawLines($pen, @(
    [System.Drawing.PointF]::new($CanvasSize * 0.28, $CanvasSize * 0.33),
    [System.Drawing.PointF]::new($CanvasSize * 0.72, $CanvasSize * 0.33),
    [System.Drawing.PointF]::new($CanvasSize * 0.56, $CanvasSize * 0.50),
    [System.Drawing.PointF]::new($CanvasSize * 0.56, $CanvasSize * 0.67)
  ))
  $Graphics.DrawLine($pen, $CanvasSize * 0.56, $CanvasSize * 0.67, $CanvasSize * 0.44, $CanvasSize * 0.74)
  $Graphics.FillEllipse($accentBrush, $CanvasSize * 0.61, $CanvasSize * 0.28, $CanvasSize * 0.09, $CanvasSize * 0.09)

  $pen.Dispose()
  $accentBrush.Dispose()
}

function Draw-EditIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.065)
  $accentPen = New-Pen -Color $Palette.Accent -Width ($CanvasSize * 0.04)
  $Graphics.DrawLine($pen, $CanvasSize * 0.33, $CanvasSize * 0.64, $CanvasSize * 0.64, $CanvasSize * 0.33)
  $Graphics.DrawLine($pen, $CanvasSize * 0.60, $CanvasSize * 0.29, $CanvasSize * 0.69, $CanvasSize * 0.38)
  $Graphics.DrawLines($pen, @(
    [System.Drawing.PointF]::new($CanvasSize * 0.30, $CanvasSize * 0.68),
    [System.Drawing.PointF]::new($CanvasSize * 0.37, $CanvasSize * 0.61),
    [System.Drawing.PointF]::new($CanvasSize * 0.39, $CanvasSize * 0.73),
    [System.Drawing.PointF]::new($CanvasSize * 0.30, $CanvasSize * 0.68)
  ))
  $Graphics.DrawLine($accentPen, $CanvasSize * 0.60, $CanvasSize * 0.29, $CanvasSize * 0.69, $CanvasSize * 0.38)
  $Graphics.DrawLine($accentPen, $CanvasSize * 0.72, $CanvasSize * 0.24, $CanvasSize * 0.72, $CanvasSize * 0.31)
  $Graphics.DrawLine($accentPen, $CanvasSize * 0.68, $CanvasSize * 0.27, $CanvasSize * 0.76, $CanvasSize * 0.27)

  $pen.Dispose()
  $accentPen.Dispose()
}

function Draw-ListIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.058)
  $accentBrush = New-Object System.Drawing.SolidBrush($Palette.Accent)

  foreach ($row in @(0.35, 0.50, 0.65)) {
    $Graphics.FillEllipse($accentBrush, $CanvasSize * 0.28, $CanvasSize * ($row - 0.04), $CanvasSize * 0.08, $CanvasSize * 0.08)
    $Graphics.DrawLine($pen, $CanvasSize * 0.42, $CanvasSize * $row, $CanvasSize * 0.68, $CanvasSize * $row)
  }

  $pen.Dispose()
  $accentBrush.Dispose()
}

function Draw-HistoryIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.06)
  $accentPen = New-Pen -Color $Palette.Accent -Width ($CanvasSize * 0.045)
  $Graphics.DrawArc($pen, $CanvasSize * 0.26, $CanvasSize * 0.26, $CanvasSize * 0.48, $CanvasSize * 0.48, 36, 290)
  $Graphics.DrawLine($pen, $CanvasSize * 0.56, $CanvasSize * 0.39, $CanvasSize * 0.68, $CanvasSize * 0.35)
  $Graphics.DrawLine($pen, $CanvasSize * 0.50, $CanvasSize * 0.37, $CanvasSize * 0.50, $CanvasSize * 0.50)
  $Graphics.DrawLine($accentPen, $CanvasSize * 0.50, $CanvasSize * 0.50, $CanvasSize * 0.60, $CanvasSize * 0.56)

  $pen.Dispose()
  $accentPen.Dispose()
}

function Draw-StatsIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $fillBrush = New-Object System.Drawing.SolidBrush($Palette.Accent)
  $strokePen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.045)

  $bars = @(
    @{ X = 0.28; Y = 0.56; W = 0.10; H = 0.16 },
    @{ X = 0.45; Y = 0.46; W = 0.10; H = 0.26 },
    @{ X = 0.62; Y = 0.36; W = 0.10; H = 0.36 }
  )

  foreach ($bar in $bars) {
    $path = New-Object System.Drawing.Drawing2D.GraphicsPath
    Add-RoundedRect -Path $path -X ($CanvasSize * $bar.X) -Y ($CanvasSize * $bar.Y) -Width ($CanvasSize * $bar.W) -Height ($CanvasSize * $bar.H) -Radius ($CanvasSize * 0.035)
    $Graphics.FillPath($fillBrush, $path)
    $Graphics.DrawPath($strokePen, $path)
    $path.Dispose()
  }

  $fillBrush.Dispose()
  $strokePen.Dispose()
}

function Draw-BoardIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.055)
  $accentBrush = New-Object System.Drawing.SolidBrush($Palette.Accent)

  $cupPath = New-Object System.Drawing.Drawing2D.GraphicsPath
  $cupPath.AddBezier(
    $CanvasSize * 0.34, $CanvasSize * 0.34,
    $CanvasSize * 0.34, $CanvasSize * 0.56,
    $CanvasSize * 0.66, $CanvasSize * 0.56,
    $CanvasSize * 0.66, $CanvasSize * 0.34
  )
  $Graphics.DrawPath($pen, $cupPath)
  $Graphics.DrawArc($pen, $CanvasSize * 0.25, $CanvasSize * 0.34, $CanvasSize * 0.18, $CanvasSize * 0.18, 100, 170)
  $Graphics.DrawArc($pen, $CanvasSize * 0.57, $CanvasSize * 0.34, $CanvasSize * 0.18, $CanvasSize * 0.18, -90, 170)
  $Graphics.DrawLine($pen, $CanvasSize * 0.50, $CanvasSize * 0.56, $CanvasSize * 0.50, $CanvasSize * 0.69)
  $Graphics.DrawLine($pen, $CanvasSize * 0.40, $CanvasSize * 0.72, $CanvasSize * 0.60, $CanvasSize * 0.72)
  $Graphics.FillEllipse($accentBrush, $CanvasSize * 0.45, $CanvasSize * 0.41, $CanvasSize * 0.10, $CanvasSize * 0.10)

  $cupPath.Dispose()
  $pen.Dispose()
  $accentBrush.Dispose()
}

function Draw-CalendarIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.055)
  $accentPen = New-Pen -Color $Palette.Accent -Width ($CanvasSize * 0.045)
  $body = New-Object System.Drawing.Drawing2D.GraphicsPath
  Add-RoundedRect -Path $body -X ($CanvasSize * 0.25) -Y ($CanvasSize * 0.29) -Width ($CanvasSize * 0.50) -Height ($CanvasSize * 0.44) -Radius ($CanvasSize * 0.08)
  $Graphics.DrawPath($pen, $body)
  $Graphics.DrawLine($pen, $CanvasSize * 0.25, $CanvasSize * 0.42, $CanvasSize * 0.75, $CanvasSize * 0.42)
  $Graphics.DrawLine($pen, $CanvasSize * 0.38, $CanvasSize * 0.24, $CanvasSize * 0.38, $CanvasSize * 0.35)
  $Graphics.DrawLine($pen, $CanvasSize * 0.62, $CanvasSize * 0.24, $CanvasSize * 0.62, $CanvasSize * 0.35)
  $Graphics.DrawLines($accentPen, @(
    [System.Drawing.PointF]::new($CanvasSize * 0.36, $CanvasSize * 0.56),
    [System.Drawing.PointF]::new($CanvasSize * 0.46, $CanvasSize * 0.64),
    [System.Drawing.PointF]::new($CanvasSize * 0.64, $CanvasSize * 0.50)
  ))

  $body.Dispose()
  $pen.Dispose()
  $accentPen.Dispose()
}

function Draw-KeyIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.055)
  $accentBrush = New-Object System.Drawing.SolidBrush($Palette.Accent)
  $Graphics.DrawEllipse($pen, $CanvasSize * 0.28, $CanvasSize * 0.38, $CanvasSize * 0.22, $CanvasSize * 0.22)
  $Graphics.DrawLine($pen, $CanvasSize * 0.47, $CanvasSize * 0.50, $CanvasSize * 0.68, $CanvasSize * 0.50)
  $Graphics.DrawLine($pen, $CanvasSize * 0.60, $CanvasSize * 0.50, $CanvasSize * 0.60, $CanvasSize * 0.60)
  $Graphics.DrawLine($pen, $CanvasSize * 0.68, $CanvasSize * 0.50, $CanvasSize * 0.68, $CanvasSize * 0.56)
  $Graphics.FillEllipse($accentBrush, $CanvasSize * 0.34, $CanvasSize * 0.44, $CanvasSize * 0.10, $CanvasSize * 0.10)

  $pen.Dispose()
  $accentBrush.Dispose()
}

function Draw-WakeIcon {
  param([System.Drawing.Graphics]$Graphics, [hashtable]$Palette, [int]$CanvasSize)

  $pen = New-Pen -Color $Palette.Stroke -Width ($CanvasSize * 0.055)
  $accentPen = New-Pen -Color $Palette.Accent -Width ($CanvasSize * 0.042)
  $Graphics.DrawArc($pen, $CanvasSize * 0.30, $CanvasSize * 0.33, $CanvasSize * 0.40, $CanvasSize * 0.34, 200, 140)
  $Graphics.DrawLine($pen, $CanvasSize * 0.36, $CanvasSize * 0.63, $CanvasSize * 0.64, $CanvasSize * 0.63)
  $Graphics.DrawLine($pen, $CanvasSize * 0.41, $CanvasSize * 0.63, $CanvasSize * 0.37, $CanvasSize * 0.72)
  $Graphics.DrawLine($pen, $CanvasSize * 0.59, $CanvasSize * 0.63, $CanvasSize * 0.63, $CanvasSize * 0.72)
  $Graphics.DrawLine($accentPen, $CanvasSize * 0.50, $CanvasSize * 0.24, $CanvasSize * 0.50, $CanvasSize * 0.31)
  $Graphics.DrawLine($accentPen, $CanvasSize * 0.42, $CanvasSize * 0.28, $CanvasSize * 0.37, $CanvasSize * 0.23)
  $Graphics.DrawLine($accentPen, $CanvasSize * 0.58, $CanvasSize * 0.28, $CanvasSize * 0.63, $CanvasSize * 0.23)

  $pen.Dispose()
  $accentPen.Dispose()
}

function Draw-Icon {
  param(
    [string]$Name,
    [string]$Tone,
    [string]$Destination
  )

  $palette = Get-Palette -Name $Name -Tone $Tone
  $context = New-GraphicsContext -CanvasSize $Size
  $bitmap = $context.Bitmap
  $graphics = $context.Graphics

  try {
    Draw-Badge -Graphics $graphics -Palette $palette -CanvasSize $Size

    switch ($Name) {
      'home' { Draw-HomeIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'food' { Draw-FoodIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'checkin' { Draw-CheckinIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'mine' { Draw-MineIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'spark' { Draw-SparkIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'filter' { Draw-FilterIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'edit' { Draw-EditIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'list' { Draw-ListIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'history' { Draw-HistoryIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'stats' { Draw-StatsIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'board' { Draw-BoardIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'calendar' { Draw-CalendarIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'key' { Draw-KeyIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      'wake' { Draw-WakeIcon -Graphics $graphics -Palette $palette -CanvasSize $Size }
      default { throw "Unsupported icon: $Name" }
    }

    $bitmap.Save($Destination, [System.Drawing.Imaging.ImageFormat]::Png)
  }
  finally {
    $graphics.Dispose()
    $bitmap.Dispose()
  }
}

function Write-PreviewSheet {
  param(
    [string[]]$Names,
    [string]$Destination
  )

  $columns = 4
  $rows = [Math]::Ceiling($Names.Count / $columns)
  $tile = $Size + 46
  $margin = 18
  $canvasWidth = ($columns * $tile) + ($margin * 2)
  $canvasHeight = ($rows * $tile) + ($margin * 2)
  $context = New-GraphicsContext -CanvasSize $canvasWidth
  $bitmap = $context.Bitmap
  $graphics = $context.Graphics
  $graphics.Clear((New-Color '#FFF8F1'))

  $titleBrush = New-Object System.Drawing.SolidBrush((New-Color '#5E4A40'))
  $font = New-Object System.Drawing.Font('Segoe UI', 9, [System.Drawing.FontStyle]::Regular)
  $format = New-Object System.Drawing.StringFormat
  $format.Alignment = [System.Drawing.StringAlignment]::Center

  try {
    for ($index = 0; $index -lt $Names.Count; $index++) {
      $name = $Names[$index]
      $column = $index % $columns
      $row = [Math]::Floor($index / $columns)
      $x = $margin + ($column * $tile)
      $y = $margin + ($row * $tile)

      $path = Join-Path $OutputDir "$name-active.png"
      $icon = [System.Drawing.Image]::FromFile($path)
      $graphics.DrawImage($icon, $x, $y, $Size, $Size)
      $graphics.DrawString($name, $font, $titleBrush, [float]($x + ($Size / 2)), [float]($y + $Size + 10), $format)
      $icon.Dispose()
    }

    $bitmap.Save($Destination, [System.Drawing.Imaging.ImageFormat]::Png)
  }
  finally {
    $titleBrush.Dispose()
    $font.Dispose()
    $format.Dispose()
    $graphics.Dispose()
    $bitmap.Dispose()
  }
}

$resolvedOutputDir = [System.IO.Path]::GetFullPath($OutputDir)
[System.IO.Directory]::CreateDirectory($resolvedOutputDir) | Out-Null

$iconNames = @(
  'home',
  'food',
  'checkin',
  'mine',
  'spark',
  'filter',
  'edit',
  'list',
  'history',
  'stats',
  'board',
  'calendar',
  'key',
  'wake'
)

foreach ($iconName in $iconNames) {
  foreach ($tone in @('default', 'active')) {
    $destination = Join-Path $resolvedOutputDir "$iconName-$tone.png"
    Draw-Icon -Name $iconName -Tone $tone -Destination $destination
  }
}

Write-PreviewSheet -Names $iconNames -Destination (Join-Path $resolvedOutputDir 'preview-sheet.png')
Write-Output "Generated $($iconNames.Count * 2) brand icons in $resolvedOutputDir"
