import { useEffect, useMemo, useRef, useState } from 'react';
import { CalendarIcon, Search } from 'lucide-react';
import { format } from 'date-fns';

import { Button } from 'components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from 'components/ui/card';
import { Input } from 'components/ui/input';
import { Calendar } from 'components/ui/calendar';
import { Popover, PopoverContent, PopoverTrigger, PopoverAnchor } from 'components/ui/popover';
import { getStations } from 'api/stations';

export function ScheduleSearchForm({ onSearch }) {
  // State for the form inputs
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [fromId, setFromId] = useState(null);
  const [toId, setToId] = useState(null);
  const [date, setDate] = useState(new Date());

  // stations data
  const [stations, setStations] = useState([]);
  const [loadingStations, setLoadingStations] = useState(false);
  const [stationsError, setStationsError] = useState('');

  // popover open states
  const [openFrom, setOpenFrom] = useState(false);
  const [openTo, setOpenTo] = useState(false);
  const fromAnchorRef = useRef(null);
  const toAnchorRef = useRef(null);

  useEffect(() => {
    let active = true;
    setLoadingStations(true);
    getStations()
      .then((data) => {
        if (!active) return;
        setStations(Array.isArray(data) ? data : []);
        setStationsError('');
      })
      .catch((e) => {
        if (!active) return;
        setStationsError(e?.message || 'Failed to load stations');
      })
      .finally(() => {
        if (!active) return;
        setLoadingStations(false);
      });
    return () => {
      active = false;
    };
  }, []);

  const filteredFrom = useMemo(() => {
    const q = (from || '').toLowerCase();
    return stations.filter((s) => s.name.toLowerCase().includes(q)).slice(0, 8);
  }, [stations, from]);

  const filteredTo = useMemo(() => {
    const q = (to || '').toLowerCase();
    return stations.filter((s) => s.name.toLowerCase().includes(q)).slice(0, 8);
  }, [stations, to]);

  const handleSearch = () => {
    const payload = {
      from: fromId ?? from,
      to: toId ?? to,
      date: format(date, 'yyyy-MM-dd'),
    };
    onSearch?.(payload);
  };

  return (
    <Card className="max-w-2xl mx-auto">
      <CardHeader>
        <CardTitle>Find Your Journey</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {/* Departure Station Input */}
          <div className="relative">
            <Popover open={openFrom} onOpenChange={setOpenFrom}>
              <PopoverAnchor asChild>
                <div ref={fromAnchorRef}>
                  <Input
                    placeholder="Departure Station"
                    value={from}
                    onChange={(e) => {
                      setFrom(e.target.value);
                      if (fromId) setFromId(null); // typing invalidates selection
                      setOpenFrom(true);
                    }}
                    onFocus={() => setOpenFrom(true)}
                    aria-autocomplete="list"
                    aria-expanded={openFrom}
                    role="combobox"
                  />
                </div>
              </PopoverAnchor>
              <PopoverContent
                className="p-0 w-72 sm:w-[--radix-popover-trigger-width]"
                align="start"
                onInteractOutside={(e) => {
                  // keep open while interacting with the input/anchor
                  if (fromAnchorRef.current && fromAnchorRef.current.contains(e.target)) {
                    e.preventDefault();
                  }
                }}
              >
                <div className="max-h-64 overflow-auto">
                  {stationsError && (
                    <div className="px-3 py-2 text-sm text-red-500">{stationsError}</div>
                  )}
                  {loadingStations && (
                    <div className="px-3 py-2 text-sm text-muted-foreground">Loading…</div>
                  )}
                  {!loadingStations && filteredFrom.length === 0 && (
                    <div className="px-3 py-2 text-sm text-muted-foreground">No stations</div>
                  )}
                  <ul className="py-1" role="listbox">
                    {filteredFrom.map((s) => (
                      <li
                        key={s.id}
                        role="option"
                        aria-selected={fromId === s.id}
                        className="px-3 py-2 cursor-pointer hover:bg-accent hover:text-accent-foreground text-sm"
                        onMouseDown={(e) => e.preventDefault()}
                        onClick={() => {
                          setFrom(s.name);
                          setFromId(s.id);
                          setOpenFrom(false);
                        }}
                      >
                        {s.name}
                      </li>
                    ))}
                  </ul>
                </div>
              </PopoverContent>
            </Popover>
          </div>

          {/* Arrival Station Input */}
          <div className="relative">
            <Popover open={openTo} onOpenChange={setOpenTo}>
              <PopoverAnchor asChild>
                <div ref={toAnchorRef}>
                  <Input
                    placeholder="Arrival Station"
                    value={to}
                    onChange={(e) => {
                      setTo(e.target.value);
                      if (toId) setToId(null);
                      setOpenTo(true);
                    }}
                    onFocus={() => setOpenTo(true)}
                    aria-autocomplete="list"
                    aria-expanded={openTo}
                    role="combobox"
                  />
                </div>
              </PopoverAnchor>
              <PopoverContent
                className="p-0 w-72 sm:w-[--radix-popover-trigger-width]"
                align="start"
                onInteractOutside={(e) => {
                  if (toAnchorRef.current && toAnchorRef.current.contains(e.target)) {
                    e.preventDefault();
                  }
                }}
              >
                <div className="max-h-64 overflow-auto">
                  {stationsError && (
                    <div className="px-3 py-2 text-sm text-red-500">{stationsError}</div>
                  )}
                  {loadingStations && (
                    <div className="px-3 py-2 text-sm text-muted-foreground">Loading…</div>
                  )}
                  {!loadingStations && filteredTo.length === 0 && (
                    <div className="px-3 py-2 text-sm text-muted-foreground">No stations</div>
                  )}
                  <ul className="py-1" role="listbox">
                    {filteredTo.map((s) => (
                      <li
                        key={s.id}
                        role="option"
                        aria-selected={toId === s.id}
                        className="px-3 py-2 cursor-pointer hover:bg-accent hover:text-accent-foreground text-sm"
                        onMouseDown={(e) => e.preventDefault()}
                        onClick={() => {
                          setTo(s.name);
                          setToId(s.id);
                          setOpenTo(false);
                        }}
                      >
                        {s.name}
                      </li>
                    ))}
                  </ul>
                </div>
              </PopoverContent>
            </Popover>
          </div>
        </div>

        <div className="mt-4">
          {/* Date Picker using Popover and Calendar */}
          <Popover>
            <PopoverTrigger asChild>
              <Button
                variant={"outline"}
                className="w-full justify-start text-left font-normal"
              >
                <CalendarIcon className="mr-2 h-4 w-4" />
                {date ? format(date, "PPP") : <span>Pick a date</span>}
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-auto p-4" align="start">
              <Calendar
                mode="single"
                selected={date}
                onSelect={setDate}
                className="rounded-md"
                classNames={{
                  months: "space-y-4",
                  month: "space-y-4",
                  caption: "flex justify-center pt-1 relative items-center text-lg font-semibold",
                  caption_label: "text-lg font-semibold",
                  nav: "space-x-1 flex items-center",
                  nav_button: "h-8 w-8 bg-transparent p-0 opacity-50 hover:opacity-100",
                  nav_button_previous: "absolute left-1",
                  nav_button_next: "absolute right-1",
                  table: "w-full border-collapse space-y-1",
                  head_row: "flex",
                  head_cell: "text-muted-foreground rounded-md w-10 font-normal text-base",
                  row: "flex w-full mt-2",
                  cell: "text-center text-base p-0 relative [&:has([aria-selected])]:bg-accent first:[&:has([aria-selected])]:rounded-l-md last:[&:has([aria-selected])]:rounded-r-md focus-within:relative focus-within:z-20",
                  day: "h-10 w-10 p-0 font-normal aria-selected:opacity-100 hover:bg-accent hover:text-accent-foreground rounded-md",
                  day_selected: "bg-primary text-primary-foreground hover:bg-primary hover:text-primary-foreground focus:bg-primary focus:text-primary-foreground",
                  day_today: "bg-accent text-accent-foreground",
                  day_outside: "text-muted-foreground opacity-50",
                  day_disabled: "text-muted-foreground opacity-50",
                  day_range_middle: "aria-selected:bg-accent aria-selected:text-accent-foreground",
                  day_hidden: "invisible",
                }}
              />
            </PopoverContent>
          </Popover>
        </div>

        <Button onClick={handleSearch} className="w-full mt-6">
          <Search className="mr-2 h-4 w-4" /> Search Trains
        </Button>
      </CardContent>
    </Card>
  );
}