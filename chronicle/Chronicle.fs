module Chronicle

type guid = System.Guid

type User = { name: string; trackCount: int }
type Artist = { name: string; mbid: guid option }
type Track = { name: string; artist: Artist; mbid: guid option }
