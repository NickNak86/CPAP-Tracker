# CSV Export Helper

This repository includes `scripts/export_csv.py` which can export BiPAP usage records from JSON to CSV format.

## Installation

The script works with or without pandas:
- **With pandas** (recommended): `pip install pandas`
- **Without pandas**: Uses Python's built-in csv module

## Basic Usage

```bash
python scripts/export_csv.py --input example_data.json --output usage_report.csv
```

## Advanced Usage

### Custom Columns

Export only specific columns:

```bash
python scripts/export_csv.py \
    --input data/usage_2025.json \
    --output reports/usage_2025.csv \
    --columns "date,usage_hours,AHI,mask_leak_rate"
```

### Verbose Output

Enable detailed logging:

```bash
python scripts/export_csv.py --input data.json --output report.csv --verbose
```

## Default Columns

If no columns are specified, the following are exported:
- `date` - Usage date
- `usage_hours` - Total hours of BiPAP usage
- `AHI` - Apnea-Hypopnea Index
- `mask_leak_rate` - Mask leak rate (L/min)
- `pressure_settings` - Pressure settings used

## Input Format

The input JSON file should contain an array of usage records:

```json
[
  {
    "date": "2025-12-01",
    "usage_hours": 7.5,
    "AHI": 2.3,
    "mask_leak_rate": 5.2,
    "pressure_settings": "10-15 cmH2O"
  },
  {
    "date": "2025-12-02",
    "usage_hours": 8.0,
    "AHI": 1.8,
    "mask_leak_rate": 3.1,
    "pressure_settings": "10-15 cmH2O"
  }
]
```

## Integration with BiPAP Tracker

This script can be used to export data collected by the BiPAP Tracker app for analysis in spreadsheet software or other tools.

## Command-Line Options

```
usage: export_csv.py [-h] --input INPUT [--output OUTPUT] [--columns COLUMNS] [--verbose]

Export BiPAP usage data to CSV

options:
  -h, --help         show this help message and exit
  --input INPUT      Input JSON file with usage records
  --output OUTPUT    Output CSV filename (default: usage_export.csv)
  --columns COLUMNS  Comma-separated list of columns to export
  --verbose          Enable verbose logging
```
