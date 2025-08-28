import * as React from 'react';
import {
    Table,
    TableBody,
    TableCell,
    tableCellClasses,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    CircularProgress,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { TableVirtuoso, type TableComponents } from 'react-virtuoso';
import { useEffect, useState } from 'react';
import type { Municipalities } from '../../data/MunicipalitiesData';
import { GetAllMunicipalities } from '../../functions/MunicipalitiesAPIFunctions';

const StyledTableCell = styled(TableCell)(() => ({
    color: 'var(--text-color)',
    borderBottom: '1px solid var(--border-color)',
    [`&.${tableCellClasses.head}`]: {
        backgroundColor: 'var(--primary-color)',
        fontWeight: 'bold',
    },
}));

const StyledTableRow = styled(TableRow)(() => ({
    backgroundColor: 'var(--container-bg)',
    cursor: 'pointer',
    borderBottom: '1px solid var(--border-color)',
    '&:hover': {
        backgroundColor: '#222f3e',
    },
    '&:last-child td, &:last-child th': {
        border: 0,
    },
}));

const columns = [
    { dataKey: 'name', label: 'Name', width: 325 },
    { dataKey: 'acronym', label: 'CNL', width: 325 },
];

export default function VirtualizedStyledTable() {
    const [municipalities, setMunicipalities] = useState<Municipalities[]>([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            try {
                const data = await GetAllMunicipalities();
                setMunicipalities(Array.isArray(data) ? data : []);
            } catch (err) {
                console.error("Falha ao buscar municípios:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    const VirtuosoTableComponents: TableComponents<Municipalities> = {
        Scroller: React.forwardRef<HTMLDivElement>((props, ref) => (
            <TableContainer component={Paper} {...props} ref={ref} sx={{ backgroundColor: 'var(--container-bg)'}}/>
        )),
        Table: (props) => (
            <Table {...props} sx={{ borderCollapse: 'separate', tableLayout: 'fixed' }} />
        ),
        TableHead,
        TableRow: StyledTableRow, 
        TableBody: React.forwardRef<HTMLTableSectionElement>((props, ref) => (
            <TableBody {...props} ref={ref} />
        )),
    };

    function fixedHeaderContent() {
        return (
            <TableRow>
                {columns.map((column) => (
                    <StyledTableCell
                        key={column.dataKey}
                        variant="head"
                        align={column.dataKey === 'acronym' ? 'center' : 'center'}
                        style={{ width: column.width }}
                    >
                        {column.label}
                    </StyledTableCell>
                ))}
            </TableRow>
        );
    }

    function rowContent(_index: number, row: Municipalities) {
        return (
            <React.Fragment>
                <StyledTableCell align="left" style={{ borderRight: '1px solid var(--border-color)', textTransform: 'uppercase' }}>
                    {row.name + ' - ' + row.state}
                </StyledTableCell>
                <StyledTableCell align="center">
                    {row.acronym}
                </StyledTableCell>
            </React.Fragment>
        );
    }

    return (
        <div style={{ border: '1px solid var(--border-color)', borderRadius: '6px', overflow: 'hidden' }}>
            {loading ? (
                <div style={{ display: 'flex', justifyContent: 'center', padding: '20px', backgroundColor: 'var(--container-bg)'}}>
                    <CircularProgress />
                </div>
            ) : (
                <Paper style={{ height: 600, width: '100%', backgroundColor: 'var(--container-bg)' }}>
                    <TableVirtuoso
                        data={municipalities}
                        components={VirtuosoTableComponents}
                        fixedHeaderContent={fixedHeaderContent}
                        itemContent={rowContent}
                    />
                </Paper>
            )}
        </div>
    );
}